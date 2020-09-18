package com.adobe.granite.ui.clientlibs.webpack.impl;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.adobe.granite.ui.clientlibs.webpack.WebpackHtmlLibraryManager;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adobe.granite.ui.clientlibs.webpack.impl.ManifestProviderService.AUTH_INFO;

@Component
public class WebpackHtmlLibraryManagerImpl implements WebpackHtmlLibraryManager {
    
    static final Logger LOGGER = LoggerFactory.getLogger(WebpackHtmlLibraryManagerImpl.class);
    
    @Reference
    private HtmlLibraryManager htmlLibraryManager;
    
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    private WebpackHtmlLibraryManagerHandlerProvider provider;
    
    private static final String SCRIPT_TAG = "%n<script type=\"text/javascript\" src=\"%s\"></script>";
    private static final String CSS_TAG = "%n<link rel=\"stylesheet\" href=\"%s\" type=\"text/css\">";
    
    
    @Override
    public void writeJsInclude(SlingHttpServletRequest request, Writer out, String... categories) throws IOException {
        htmlLibraryManager.writeJsInclude(request,out, categories);
        Collection<ClientLibrary> clientLibraries = htmlLibraryManager.getLibraries(categories, LibraryType.JS, true, true);
        writeDelegate(clientLibraries, LibraryType.JS, request, out );
    }
    
    
    @Override
    public void writeCssInclude(SlingHttpServletRequest request, Writer out, String... categories) throws IOException {
        htmlLibraryManager.writeCssInclude(request,out, categories);
        Collection<ClientLibrary> clientLibraries = htmlLibraryManager.getLibraries(categories, LibraryType.CSS, true, true);
        writeDelegate(clientLibraries, LibraryType.CSS, request, out );
    }
    
    @Override
    public void writeIncludes(SlingHttpServletRequest request, Writer out, String... categories) throws IOException {
        htmlLibraryManager.writeIncludes(request,out, categories);
        Collection<ClientLibrary> clientLibrariesJs = htmlLibraryManager.getLibraries(categories, LibraryType.JS, true, true);
        Collection<ClientLibrary> clientLibrariesCss = htmlLibraryManager.getLibraries(categories, LibraryType.CSS, true, true);
    
        Collection<ClientLibrary> combined = Stream.concat(clientLibrariesJs.stream(), clientLibrariesCss.stream()) .collect(Collectors.toList());
        writeDelegate(combined, LibraryType.JS, request, out );
        writeDelegate(combined, LibraryType.CSS, request, out );
       
    }
    
    
    private void writeDelegate(Collection<ClientLibrary> clientLibraries, LibraryType type, SlingHttpServletRequest request, Writer out){
        
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)) {
            
            for (ClientLibrary clientLibrary : clientLibraries) {
                
                WebpackHtmlLibraryManagerHandlerProvider.WebpackHtmlLibraryManagerHandler handler = provider.provide(request, resourceResolver,clientLibrary);
                
                handler.init(type);
                
                if(handler.isWebpackInclude()){
                    Map<String, String> completeChunkMap = handler.getChunkMap();
                    List<String> entryPoints = handler.getEntryPoints();
                    entryPoints.forEach(str -> printScript(str,out, type));
                    printScripts(entryPoints,completeChunkMap, out, type);
                    
                    List<String> requestSpecificChunkKeys = handler.getRequestSpecificChunkKeys(request);
                    printScripts(requestSpecificChunkKeys,completeChunkMap, out, type);
                }
                
            }
            
        } catch (LoginException | IOException e) {
            LOGGER.error("Error opening the resource resolver " + AUTH_INFO.get(ResourceResolverFactory.SUBSERVICE));
        }
    }
    
    private void printScripts(List<String> chunks, Map<String, String> completeChunkMap, Writer out, LibraryType type){
        chunks.stream().map((chunk) -> completeChunkMap.get( chunk + ".js" ) ).filter(StringUtils::isNotBlank).forEach(script -> printScript(script, out, type));
        chunks.stream().map((chunk) -> completeChunkMap.get( chunk + ".css") ).filter(StringUtils::isNotBlank).forEach(script -> printScript(script, out, type));
        chunks.stream().map(completeChunkMap::get).filter(StringUtils::isNotBlank).forEach(script -> printScript(script, out, type));
    }
    
    private void printScript(String script, Writer out, LibraryType type){
        
        final String output;
        if(type == LibraryType.JS){
            output = String.format(SCRIPT_TAG,script);
        }else if(type == LibraryType.CSS){
            output = String.format(CSS_TAG,script);
        }else{
            output = "";
        }
        
        try {
            out.write(output);
        } catch (IOException e) {
            LOGGER.error("Error writing out the script " + script + " to the response");
        }
    }
}
