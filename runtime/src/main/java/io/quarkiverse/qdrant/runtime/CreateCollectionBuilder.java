package io.quarkiverse.qdrant.runtime;

import io.quarkiverse.qdrant.runtime.model.CreateCollectionRequest;

public class CreateCollectionBuilder {

    private final QdrantRestClientApi client;
    private final String collection;
    private int vectorSize;
    private String distance = "Cosine";

    CreateCollectionBuilder(QdrantRestClientApi client, String collection) {
        this.client = client;
        this.collection = collection;
    }

    public CreateCollectionBuilder vectorSize(int vectorSize) {
        this.vectorSize = vectorSize;
        return this;
    }

    public CreateCollectionBuilder distance(String distance) {
        this.distance = distance;
        return this;
    }

    public void execute() {
        client.createCollection(collection, new CreateCollectionRequest(vectorSize, distance));
    }
}
