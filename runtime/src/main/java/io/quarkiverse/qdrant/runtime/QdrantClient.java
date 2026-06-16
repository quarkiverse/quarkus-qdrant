package io.quarkiverse.qdrant.runtime;

import java.net.URI;
import java.util.List;

import io.quarkiverse.qdrant.runtime.model.CollectionDescription;
import io.quarkiverse.qdrant.runtime.model.CollectionInfo;

public class QdrantClient {

    private final QdrantRestClientApi restClient;
    private final URI baseUri;

    QdrantClient(QdrantRestClientApi restClient, URI baseUri) {
        this.restClient = restClient;
        this.baseUri = baseUri;
    }

    public URI getBaseUri() {
        return baseUri;
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

    public CollectionInfo getCollectionInfo(String collection) {
        return restClient.getCollectionInfo(collection).getResult();
    }

    public void deleteCollection(String collection) {
        restClient.deleteCollection(collection);
    }
}
