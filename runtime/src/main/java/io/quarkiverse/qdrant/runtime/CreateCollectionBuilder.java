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
        if (collection == null || collection.isBlank()) {
            throw new QdrantException("Create collection requires a collection name");
        }
        if (vectorSize <= 0) {
            throw new QdrantException("Create collection requires a vector size greater than zero");
        }
        if (distance == null || distance.isBlank()) {
            throw new QdrantException("Create collection requires a distance metric");
        }
        client.createCollection(collection, new CreateCollectionRequest(vectorSize, distance));
    }
}
