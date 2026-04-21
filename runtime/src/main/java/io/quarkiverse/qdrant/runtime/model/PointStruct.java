package io.quarkiverse.qdrant.runtime.model;

import java.util.List;
import java.util.Map;

public class PointStruct {

    private String id;
    private List<Float> vector;
    private Map<String, Object> payload;

    public PointStruct() {
    }

    public PointStruct(String id, List<Float> vector, Map<String, Object> payload) {
        this.id = id;
        this.vector = vector;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Float> getVector() {
        return vector;
    }

    public void setVector(List<Float> vector) {
        this.vector = vector;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
