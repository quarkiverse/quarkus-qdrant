package io.quarkiverse.qdrant.runtime.model;

import java.util.List;

public class UpsertRequest {

    private List<PointStruct> points;

    public UpsertRequest() {
    }

    public UpsertRequest(List<PointStruct> points) {
        this.points = points;
    }

    public List<PointStruct> getPoints() {
        return points;
    }

    public void setPoints(List<PointStruct> points) {
        this.points = points;
    }
}
