package io.quarkiverse.qdrant.runtime.model;

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
