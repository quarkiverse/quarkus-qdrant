package io.quarkiverse.qdrant.runtime;

import java.util.List;
import java.util.Map;

import io.quarkiverse.qdrant.runtime.model.ScoredPoint;
import io.quarkiverse.qdrant.runtime.model.SearchRequest;

public class SearchBuilder {

    private final QdrantRestClientApi client;
    private final String collection;
    private float[] vector;
    private int limit = 10;
    private boolean withPayload = true;
    private boolean withVector = true;
    private Float scoreThreshold;
    private Map<String, Object> filter;

    SearchBuilder(QdrantRestClientApi client, String collection) {
        this.client = client;
        this.collection = collection;
    }

    public SearchBuilder vector(float[] vector) {
        this.vector = vector;
        return this;
    }

    public SearchBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SearchBuilder withPayload(boolean withPayload) {
        this.withPayload = withPayload;
        return this;
    }

    public SearchBuilder withVector(boolean withVector) {
        this.withVector = withVector;
        return this;
    }

    public SearchBuilder scoreThreshold(float scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
        return this;
    }

    public SearchBuilder filter(Map<String, Object> filter) {
        this.filter = filter;
        return this;
    }

    public List<ScoredPoint> execute() {
        SearchRequest request = new SearchRequest();
        request.setVector(vector);
        request.setLimit(limit);
        request.setWithPayload(withPayload);
        request.setWithVector(withVector);
        request.setScoreThreshold(scoreThreshold);
        request.setFilter(filter);
        return client.search(collection, request).getResult();
    }
}
