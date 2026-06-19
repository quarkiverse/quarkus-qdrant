package io.quarkiverse.qdrant.runtime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionConfig {

    private CollectionParams params;

    public CollectionConfig() {
    }

    public CollectionParams getParams() {
        return params;
    }

    public void setParams(CollectionParams params) {
        this.params = params;
    }
}
