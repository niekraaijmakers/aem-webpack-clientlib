package com.adobe.granite.ui.clientlibs.webpack.impl.provider;

import com.adobe.granite.ui.clientlibs.webpack.WebpackContentProviderService;
import com.adobe.granite.ui.clientlibs.webpack.impl.manifest.StandardManifest;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component(service = WebpackContentProviderService.class,   property = {
        Constants.SERVICE_RANKING + ":Integer=" + Integer.MAX_VALUE
})
public class StandardWebpackContentProviderImpl implements WebpackContentProviderService<StandardManifest> {
    
    private static final String PROXY_PREFIX = "/etc.clientlibs/";
    
    
    @Override
    public Class<StandardManifest> getManifestClass() {
        return StandardManifest.class;
    }
    
    @Override
    public List<String> getCssEntryPoints(StandardManifest manifest, Resource clientLibResource) {
        String prefix = getPrefix(clientLibResource);
        return manifest.getEntryPoints().stream().filter((entryPoint)-> entryPoint.endsWith("css")).map((entryPoint) -> prefix + entryPoint).collect(Collectors.toList());
    }
    
    @Override
    public List<String> getJsEntryPoints(StandardManifest manifest, Resource clientLibResource) {
        String prefix = getPrefix(clientLibResource);
        return manifest.getEntryPoints().stream().filter((entryPoint)-> entryPoint.endsWith("js")).map((entryPoint) -> prefix + entryPoint).collect(Collectors.toList());
    }
    
    private String getPrefix(Resource clientLibResource) {
        final String prefix;
        if(clientLibResource.getPath().startsWith("/apps")){
            prefix = clientLibResource.getPath().replaceFirst("/apps/", PROXY_PREFIX) + "/resources/";
        }else{
            prefix = clientLibResource.getPath() + "/resources/";
        }
        return prefix;
    }
    
   
    
    @Override
    public Map<String, String> computeChunkMap(StandardManifest manifest) {
        return manifest.getFiles();
    }
    
    @Override
    public String getSupportedCategory() {
        return "standard";
    }
  
}
