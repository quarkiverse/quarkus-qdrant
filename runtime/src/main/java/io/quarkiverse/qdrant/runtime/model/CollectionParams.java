package io.quarkiverse.qdrant.runtime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionParams {

    private VectorsConfig vectors;

    public CollectionParams() {
    }

    public VectorsConfig getVectors() {
        return vectors;
    }

    public void setVectors(VectorsConfig vectors) {
        this.vectors = vectors;
    }
}
