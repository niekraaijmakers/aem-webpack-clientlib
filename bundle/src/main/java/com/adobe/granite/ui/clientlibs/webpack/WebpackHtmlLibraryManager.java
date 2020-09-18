package com.adobe.granite.ui.clientlibs.webpack;


import aQute.bnd.annotation.ProviderType;
import org.apache.sling.api.SlingHttpServletRequest;

import java.io.IOException;
import java.io.Writer;

@ProviderType
public interface WebpackHtmlLibraryManager {
    
    String PN_MANIFEST_CATEGORY = "cq:webpackManifestCategory";
    
    String PN_MANIFEST_RELATIVE_PATH = "cq:webpackManifestRelPath";
    
    String MANIFEST_RELATIVE_PATH_DEFAULT = "resources/asset-manifest.json";
    /**
     * Writes the JS include snippets to the given writer. The paths to the
     * JS libraries are included that match the given categories. Note that
     * themed and non-themed libraries are included. If the request contains
     * a {@value #PARAM_FORCE_THEME} parameter, the themed libraries are
     * overlaid with their respective counterparts with that given theme.
     *
     * @param request request
     * @param out writer
     * @param categories categories
     * @throws java.io.IOException if an I/O error occurs
     */
    void writeJsInclude(SlingHttpServletRequest request, Writer out, String... categories)
            throws IOException;
    
    /**
     * Writes the CSS include snippets to the given writer. The paths to the
     * CSS libraries are included that match the given categories. Note that
     * themed and non-themed libraries are included. If the request contains
     * a {@value #PARAM_FORCE_THEME} parameter, the themed libraries are
     * overlaid with their respective counterparts with that given theme.
     *
     * @param request request
     * @param out writer
     * @param categories categories
     * @throws java.io.IOException if an I/O error occurs
     */
    void writeCssInclude(SlingHttpServletRequest request, Writer out, String... categories)
            throws IOException;
    
    /**
     * Writes the include snippets to the given writer. The paths to the
     * libraries are included that match the given categories and the theme
     * name that is extracted from the request.<br>
     *
     * Same as:<br>
     * <code>
     * writeCssInclude(...);
     * writeJsInclude(...);
     * </code>
     *
     * If one of the libraries to be included has assigned channels, then the
     * inclusion is delegated to the client side library manager.
     *
     * @param request request
     * @param out writer
     * @param categories categories
     * @throws java.io.IOException if an I/O error occurs
     */
    void writeIncludes(SlingHttpServletRequest request, Writer out, String... categories)
            throws IOException;
    
    
}
