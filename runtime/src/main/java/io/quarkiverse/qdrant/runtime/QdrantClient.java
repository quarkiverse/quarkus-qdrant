package io.quarkiverse.qdrant.runtime;

import java.util.List;

import io.quarkiverse.qdrant.runtime.model.CollectionDescription;

public class QdrantClient {

    private final QdrantRestClientApi restClient;

    QdrantClient(QdrantRestClientApi restClient) {
        this.restClient = restClient;
    }

    public CreateCollectionBuilder createCollection(String collection) {
        return new CreateCollectionBuilder(restClient, collection);
    }

    public UpsertBuilder upsert(String collection) {
        return new UpsertBuilder(restClient, collection);
    }

    public DeleteBuilder delete(String collection) {
        return new DeleteBuilder(restClient, collection);
    }

    public SearchBuilder search(String collection) {
        return new SearchBuilder(restClient, collection);
    }

    public List<String> listCollections() {
        return restClient.listCollections().getResult().getCollections().stream()
                .map(CollectionDescription::getName)
                .toList();
    }

    public void deleteCollection(String collection) {
        restClient.deleteCollection(collection);
    }
}
