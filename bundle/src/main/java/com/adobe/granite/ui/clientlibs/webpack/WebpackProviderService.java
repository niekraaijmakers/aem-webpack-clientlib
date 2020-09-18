package com.adobe.granite.ui.clientlibs.webpack;


public interface WebpackProviderService {
    /**
     * Checks whether this provider can provide for a specific category
     * @param category webpack assimilated clientlib category
     * @return true or flase
     */
    String getSupportedCategory();
}
