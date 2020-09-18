package com.adobe.granite.ui.clientlibs.webpack.impl;

import com.adobe.granite.ui.clientlibs.webpack.WebpackManifest;
import com.adobe.granite.ui.clientlibs.webpack.WebpackContentProviderService;
import com.adobe.granite.ui.clientlibs.webpack.WebpackSSRChunkKeyProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.osgi.Order;
import org.apache.sling.commons.osgi.RankedServices;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.adobe.granite.ui.clientlibs.webpack.WebpackHtmlLibraryManager.PN_MANIFEST_CATEGORY;


@Component(service = WebpackManifestProvisionDesignator.class,  reference = {
        @Reference(
                name = "webpackProviderServices",
                bind = "bindWebpackProviderService",
                unbind = "unbindWebpackProviderService",
                service = WebpackContentProviderService.class,
                policy = ReferencePolicy.DYNAMIC,
                cardinality = ReferenceCardinality.AT_LEAST_ONE),
        @Reference(
                name = "ssrChunkKeyProviderServices",
                bind = "bindChunkKeyProviderService",
                unbind = "unbindChunkKeyProviderService",
                service = WebpackSSRChunkKeyProvider.class,
                policy = ReferencePolicy.DYNAMIC,
                cardinality = ReferenceCardinality.MULTIPLE)
})
public class WebpackManifestProvisionDesignator {
    
    static final Logger LOGGER = LoggerFactory.getLogger(WebpackManifestProvisionDesignator.class);
    
    private final ConcurrentMap<String, RankedServices<WebpackContentProviderService<WebpackManifest>>> webpackProviderServices = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RankedServices<WebpackSSRChunkKeyProvider<WebpackManifest>>> ssrChunkKeyProviderServices = new ConcurrentHashMap<>();
    
    public @Nullable WebpackContentProviderService<WebpackManifest> designateContentProvider(Resource clientLibResource) {
        
        String webpackCategory = clientLibResource.getValueMap().get(PN_MANIFEST_CATEGORY, String.class);
        
        if(StringUtils.isNotBlank(webpackCategory)){
    
            RankedServices<WebpackContentProviderService<WebpackManifest>> categoryServices = webpackProviderServices.get(webpackCategory);
            return categoryServices.getList().stream().findFirst().orElse(null);
            
        }else{
            LOGGER.warn("The clientlib has no webpack category! " + clientLibResource.getPath() + " not generating webpack based clientlib.");
        }
        return null;
    }
    
    public @Nullable WebpackSSRChunkKeyProvider<WebpackManifest> designateSSRChunkProvider(Resource clientLibResource) {
        
        String webpackCategory = clientLibResource.getValueMap().get(PN_MANIFEST_CATEGORY, String.class);
        
        if(StringUtils.isNotBlank(webpackCategory)){
            
            RankedServices<WebpackSSRChunkKeyProvider<WebpackManifest>> categoryServices = ssrChunkKeyProviderServices.get(webpackCategory);
            return categoryServices.getList().stream().findFirst().orElse(null);
            
        }else{
            LOGGER.warn("The clientlib has no webpack category! " + clientLibResource.getPath() + " not generating webpack based clientlib.");
        }
        return null;
    }
    
    /**
     * Binds webpackProviderService.
     *
     * @param service
     */
    public void bindWebpackProviderService(final WebpackContentProviderService<WebpackManifest> service, final Map<String, Object> properties) {
        
        final String category = service.getSupportedCategory();
        
        if(StringUtils.isNotBlank(category)){
    
            if(!webpackProviderServices.containsKey(category)){
                RankedServices<WebpackContentProviderService<WebpackManifest>> servicesEntry = new RankedServices<>(Order.ASCENDING);
                webpackProviderServices.put(category, servicesEntry);
            }
    
            RankedServices<WebpackContentProviderService<WebpackManifest>> servicesEntry = webpackProviderServices.get(category);
    
            servicesEntry.bind(service, properties);
            
        }
    }
    
    /**
     * Unbinds webpackProviderService.
     *
     * @param service
     */
    public void unbindWebpackProviderService(final WebpackContentProviderService<WebpackManifest> service, final Map<String, Object> properties) {
    
        final String category = service.getSupportedCategory();
    
        if(StringUtils.isNotBlank(category)){
        
            if(webpackProviderServices.containsKey(category)){
                RankedServices<WebpackContentProviderService<WebpackManifest>> servicesEntry = new RankedServices<>(Order.ASCENDING);
                servicesEntry.unbind(service, properties);
            }
        }
        
    }
    
    
    /**
     * Binds chunkKeyProviderService.
     *
     * @param service
     */
    public void bindChunkKeyProviderService(final WebpackSSRChunkKeyProvider<WebpackManifest> service, final Map<String, Object> properties) {
        
        final String category = service.getSupportedCategory();
        
        if(StringUtils.isNotBlank(category)){
            
            if(!ssrChunkKeyProviderServices.containsKey(category)){
                RankedServices<WebpackSSRChunkKeyProvider<WebpackManifest>> servicesEntry = new RankedServices<>(Order.ASCENDING);
                ssrChunkKeyProviderServices.put(category, servicesEntry);
            }
            
            RankedServices<WebpackSSRChunkKeyProvider<WebpackManifest>> servicesEntry = ssrChunkKeyProviderServices.get(category);
            
            servicesEntry.bind(service, properties);
            
        }
    }
    
    /**
     * Unbinds chunkKeyProviderService.
     *
     * @param service
     */
    public void unbindChunkKeyProviderService(final WebpackSSRChunkKeyProvider<WebpackManifest> service, final Map<String, Object> properties) {
        
        final String category = service.getSupportedCategory();
        
        if(StringUtils.isNotBlank(category)){
            
            if(ssrChunkKeyProviderServices.containsKey(category)){
                RankedServices<WebpackSSRChunkKeyProvider<WebpackManifest>> servicesEntry = new RankedServices<>(Order.ASCENDING);
                servicesEntry.unbind(service, properties);
            }
        }
        
    }
    
}
