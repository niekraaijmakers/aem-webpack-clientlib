package com.adobe.granite.ui.clientlibs.webpack.impl.manifest;

import com.adobe.granite.ui.clientlibs.webpack.WebpackManifest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardManifest implements WebpackManifest {
    
    @JsonProperty("files")
    private Map<String,String> files;
    @JsonProperty("entrypoints")
    private List<String> entrypoints;
    
    
    public List<String> getEntryPoints() {
        return entrypoints;
    }
    
    public Map<String,String> getFiles(){
        return files;
    }
    
}
