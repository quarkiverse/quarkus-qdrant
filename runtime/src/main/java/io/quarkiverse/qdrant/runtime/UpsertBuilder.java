package io.quarkiverse.qdrant.runtime;

import java.util.ArrayList;
import java.util.List;

import io.quarkiverse.qdrant.runtime.model.PointStruct;
import io.quarkiverse.qdrant.runtime.model.UpsertRequest;

public class UpsertBuilder {

    private final QdrantRestClientApi client;
    private final String collection;
    private final List<PointStruct> points = new ArrayList<>();

    UpsertBuilder(QdrantRestClientApi client, String collection) {
        this.client = client;
        this.collection = collection;
    }

    public UpsertBuilder point(PointStruct point) {
        this.points.add(point);
        return this;
    }

    public UpsertBuilder points(List<PointStruct> points) {
        this.points.addAll(points);
        return this;
    }

    public void execute() {
        client.upsert(collection, new UpsertRequest(points));
    }
}
