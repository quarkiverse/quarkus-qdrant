package io.quarkiverse.qdrant.runtime.model;

public class CreateCollectionRequest {

    private VectorsConfig vectors;

    public CreateCollectionRequest() {
    }

    public CreateCollectionRequest(int size, String distance) {
        this.vectors = new VectorsConfig(size, distance);
    }

    public VectorsConfig getVectors() {
        return vectors;
    }

    public void setVectors(VectorsConfig vectors) {
        this.vectors = vectors;
    }
}
