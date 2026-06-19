package io.quarkiverse.qdrant.runtime.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionParams {

    private VectorsConfig vectors;

    @JsonProperty("shard_number")
    private Integer shardNumber;

    @JsonProperty("sharding_method")
    private String shardingMethod;

    @JsonProperty("replication_factor")
    private Integer replicationFactor;

    @JsonProperty("write_consistency_factor")
    private Integer writeConsistencyFactor;

    @JsonProperty("read_fan_out_factor")
    private Integer readFanOutFactor;

    @JsonProperty("read_fan_out_delay_ms")
    private Long readFanOutDelayMs;

    @JsonProperty("on_disk_payload")
    private Boolean onDiskPayload;

    @JsonProperty("sparse_vectors")
    private Map<String, Object> sparseVectors;

    public CollectionParams() {
    }

    public VectorsConfig getVectors() {
        return vectors;
    }

    public void setVectors(VectorsConfig vectors) {
        this.vectors = vectors;
    }

    public Integer getShardNumber() {
        return shardNumber;
    }

    public void setShardNumber(Integer shardNumber) {
        this.shardNumber = shardNumber;
    }

    public String getShardingMethod() {
        return shardingMethod;
    }

    public void setShardingMethod(String shardingMethod) {
        this.shardingMethod = shardingMethod;
    }

    public Integer getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(Integer replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public Integer getWriteConsistencyFactor() {
        return writeConsistencyFactor;
    }

    public void setWriteConsistencyFactor(Integer writeConsistencyFactor) {
        this.writeConsistencyFactor = writeConsistencyFactor;
    }

    public Integer getReadFanOutFactor() {
        return readFanOutFactor;
    }

    public void setReadFanOutFactor(Integer readFanOutFactor) {
        this.readFanOutFactor = readFanOutFactor;
    }

    public Long getReadFanOutDelayMs() {
        return readFanOutDelayMs;
    }

    public void setReadFanOutDelayMs(Long readFanOutDelayMs) {
        this.readFanOutDelayMs = readFanOutDelayMs;
    }

    public Boolean getOnDiskPayload() {
        return onDiskPayload;
    }

    public void setOnDiskPayload(Boolean onDiskPayload) {
        this.onDiskPayload = onDiskPayload;
    }

    public Map<String, Object> getSparseVectors() {
        return sparseVectors;
    }

    public void setSparseVectors(Map<String, Object> sparseVectors) {
        this.sparseVectors = sparseVectors;
    }
}
