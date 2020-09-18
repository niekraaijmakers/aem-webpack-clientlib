package com.adobe.granite.ui.clientlibs.webpack;

import aQute.bnd.annotation.ConsumerType;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

@ConsumerType
public interface WebpackSSRChunkKeyProvider<T extends WebpackManifest> extends WebpackProviderService{
    
    /**
     * Returns the keys of the chunks, other then the default chunks, that are used for the specified request.
     * This is used to immediately print the appropriate chunks into the HTML by the #WebpackHtmlLibraryManager.
     * This can be used in combination with server side rendering to figure out which chunks will be used on the page.
     * @param request
     * @return
     */
    List<String> getIncludedChunkKeysForRequest(SlingHttpServletRequest request, T manifest);
    
}
