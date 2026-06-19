package io.quarkiverse.qdrant.runtime.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoredPoint {

    private String id;
    private Long version;
    private float score;
    private List<Float> vector;
    private Map<String, Object> payload;

    @JsonProperty("shard_key")
    private Object shardKey;

    @JsonProperty("order_value")
    private Object orderValue;

    public ScoredPoint() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public Object getShardKey() {
        return shardKey;
    }

    public void setShardKey(Object shardKey) {
        this.shardKey = shardKey;
    }

    public Object getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Object orderValue) {
        this.orderValue = orderValue;
    }
}
