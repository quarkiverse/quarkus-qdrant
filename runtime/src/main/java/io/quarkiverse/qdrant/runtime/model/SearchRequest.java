package io.quarkiverse.qdrant.runtime.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRequest {

    private float[] vector;
    private int limit;
    private Map<String, Object> filter;

    @JsonProperty("with_payload")
    private boolean withPayload = true;

    @JsonProperty("with_vector")
    private boolean withVector = true;

    @JsonProperty("score_threshold")
    private Float scoreThreshold;

    public SearchRequest() {
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public boolean isWithPayload() {
        return withPayload;
    }

    public void setWithPayload(boolean withPayload) {
        this.withPayload = withPayload;
    }

    public boolean isWithVector() {
        return withVector;
    }

    public void setWithVector(boolean withVector) {
        this.withVector = withVector;
    }

    public Float getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(Float scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }
}
