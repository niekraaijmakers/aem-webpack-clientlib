package com.adobe.granite.ui.clientlibs.webpack.impl;

import com.adobe.granite.ui.clientlibs.webpack.WebpackManifest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Component(service={ManifestProviderService.class})
public class ManifestProviderService {
    
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    private static final String SERVICE_NAME = "manifest-service";
    
    static final Map<String, Object> AUTH_INFO;
    
    static {
        AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, (Object) SERVICE_NAME);
    }
    
    <T extends WebpackManifest> T provideManifest(Class<T> manifestClass, String pathToManifest) throws IOException, LoginException {
    
        try(ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)){
            
            final Resource assetManifestResource =  resourceResolver.getResource(pathToManifest);
    
            if(assetManifestResource != null){
                InputStream file = assetManifestResource.adaptTo(InputStream.class);
        
                if(file == null){
                    throw new IOException("Could not load manifest file!");
                }
        
                String fileString = IOUtils.toString(file, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
        
                return objectMapper.readValue(fileString, manifestClass);
            }else{
                throw new IOException("Could not load manifest file!");
            }
        }
        
       
    }
    
}
