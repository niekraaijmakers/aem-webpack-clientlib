package com.adobe.granite.ui.clientlibs.webpack;

import aQute.bnd.annotation.ConsumerType;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Map;

/**
 * Marks a service to be a providing functionality for a specific webpack clientlibrary
 */
@ConsumerType
public interface WebpackContentProviderService<T extends WebpackManifest> extends WebpackProviderService{
    
    /*
     * Returns the underlying manifest POJO class
     * @return
             */
    Class<T> getManifestClass();
    
    /**
     * Get the entry points that should always be outputted on initial load.
     * @param manifest
     * @return
     */
    List<String> getCssEntryPoints(T manifest, Resource clientLibResource);
    
    /**
     * Get the entry points that should always be outputted on initial load.
     * @param manifest
     * @return
     */
    List<String> getJsEntryPoints(T manifest, Resource clientLibResource);
    
    /**
     * Provides all possible chunks (css and js) as key/value map.
     * @param manifest
     * @return
     */
    Map<String,String> computeChunkMap(T manifest);
    
    /**
     * Checks whether this provider can provide for a specific category
     * @param category webpack assimilated clientlib category
     * @return true or flase
     */
    String getSupportedCategory();
    
}
