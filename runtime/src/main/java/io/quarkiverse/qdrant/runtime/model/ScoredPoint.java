package io.quarkiverse.qdrant.runtime.model;

import java.util.List;
import java.util.Map;

public class ScoredPoint {

    private String id;
    private float score;
    private List<Float> vector;
    private Map<String, Object> payload;

    public ScoredPoint() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
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
