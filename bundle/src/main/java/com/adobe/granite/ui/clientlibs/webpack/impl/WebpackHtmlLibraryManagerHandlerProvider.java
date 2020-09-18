package com.adobe.granite.ui.clientlibs.webpack.impl;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.adobe.granite.ui.clientlibs.webpack.WebpackManifest;
import com.adobe.granite.ui.clientlibs.webpack.WebpackContentProviderService;
import com.adobe.granite.ui.clientlibs.webpack.WebpackSSRChunkKeyProvider;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.adobe.granite.ui.clientlibs.webpack.WebpackHtmlLibraryManager.MANIFEST_RELATIVE_PATH_DEFAULT;
import static com.adobe.granite.ui.clientlibs.webpack.WebpackHtmlLibraryManager.PN_MANIFEST_RELATIVE_PATH;


@Component(service = WebpackHtmlLibraryManagerHandlerProvider.class)
public class WebpackHtmlLibraryManagerHandlerProvider {
    
    @Reference
    private WebpackManifestProvisionDesignator designator;
    
    @Reference
    private ManifestProviderService manifestProvider;
    
    public WebpackHtmlLibraryManagerHandler provide(SlingHttpServletRequest request, ResourceResolver resourceResolver, ClientLibrary clientLibrary) {
        return new WebpackHtmlLibraryManagerHandler(request, resourceResolver, clientLibrary);
    }
    
    public class WebpackHtmlLibraryManagerHandler {
        
        private final SlingHttpServletRequest request;
        private final ResourceResolver resourceResolver;
        private final ClientLibrary clientLibrary;
        
        private WebpackContentProviderService<WebpackManifest> designatedContentProvider;
        private WebpackSSRChunkKeyProvider<WebpackManifest> designatedSSRChunkKeyProvider;
        
        private Resource clientLibResource;
        private WebpackManifest webpackManifest;
        private Map<String, String> chunkMap;
        private List<String> entryPoints = new ArrayList<>();
        
        private List<String> requestSpecificChunkKeys = new ArrayList<>();
        
        private boolean webpackInclude = false;
        
        public WebpackHtmlLibraryManagerHandler(@Nonnull SlingHttpServletRequest request, @Nonnull ResourceResolver resourceResolver, @Nonnull ClientLibrary clientLibrary) {
            this.request = request;
            this.resourceResolver = resourceResolver;
            this.clientLibrary = clientLibrary;
        }
        
        public void init(LibraryType type) throws IOException, LoginException {
            final String path = clientLibrary.getPath();
            
            clientLibResource = resourceResolver.getResource(path);
            
            if(clientLibResource != null){
                designatedContentProvider = designator.designateContentProvider(clientLibResource);
                
                if(designatedContentProvider != null){
                    webpackInclude = true;
    
                    computeWebpackContent(type);
    
                    initSSRChunkKeys();
                }
                
            }
          
        }
    
        private void computeWebpackContent(LibraryType type) throws IOException, LoginException {
            webpackManifest = manifestProvider.provideManifest(designatedContentProvider.getManifestClass(), getPathToManifest());
            chunkMap = designatedContentProvider.computeChunkMap(webpackManifest);
            entryPoints = type == LibraryType.CSS ? designatedContentProvider.getCssEntryPoints(webpackManifest, clientLibResource) : designatedContentProvider.getJsEntryPoints(webpackManifest, clientLibResource);
        }
    
        private void initSSRChunkKeys() {
            designatedSSRChunkKeyProvider = designator.designateSSRChunkProvider(clientLibResource);
        
            if (designatedSSRChunkKeyProvider != null) {
                requestSpecificChunkKeys.addAll(designatedSSRChunkKeyProvider.getIncludedChunkKeysForRequest(request, webpackManifest));
            }
        }
    
        public boolean isWebpackInclude() {
            return webpackInclude;
        }
    
        public Map<String, String> getChunkMap() {
            return chunkMap;
        }
        
        public List<String> getEntryPoints() {
            return entryPoints;
        }
        
        public List<String> getRequestSpecificChunkKeys(SlingHttpServletRequest request) {
            return requestSpecificChunkKeys;
        }
        
        private String getPathToManifest(){
    
            ValueMap valueMap = clientLibResource.getValueMap();
            
            String relativePath = valueMap.get(PN_MANIFEST_RELATIVE_PATH,MANIFEST_RELATIVE_PATH_DEFAULT);
    
            return clientLibrary.getPath() + "/" + relativePath;
        }
    }
    
    
}
