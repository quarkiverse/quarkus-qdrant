package io.quarkiverse.qdrant.runtime;

import java.util.List;
import java.util.Map;

import io.quarkiverse.qdrant.runtime.model.DeleteRequest;

public class DeleteBuilder {

    private final QdrantRestClientApi client;
    private final String collection;
    private List<String> ids;
    private Map<String, Object> filter;

    DeleteBuilder(QdrantRestClientApi client, String collection) {
        this.client = client;
        this.collection = collection;
    }

    public DeleteBuilder byIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public DeleteBuilder byFilter(Map<String, Object> filter) {
        this.filter = filter;
        return this;
    }

    public void execute() {
        if (ids != null) {
            client.delete(collection, DeleteRequest.byIds(ids));
        } else if (filter != null) {
            client.delete(collection, DeleteRequest.byFilter(filter));
        }
    }
}
