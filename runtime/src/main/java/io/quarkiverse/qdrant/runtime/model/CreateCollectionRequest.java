package io.quarkiverse.qdrant.runtime.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateCollectionRequest {

    private VectorsConfig vectors;

    @JsonProperty("shard_number")
    private Integer shardNumber;

    @JsonProperty("sharding_method")
    private String shardingMethod;

    @JsonProperty("replication_factor")
    private Integer replicationFactor;

    @JsonProperty("write_consistency_factor")
    private Integer writeConsistencyFactor;

    @JsonProperty("on_disk_payload")
    private Boolean onDiskPayload;

    @JsonProperty("hnsw_config")
    private Object hnswConfig;

    @JsonProperty("wal_config")
    private Object walConfig;

    @JsonProperty("optimizers_config")
    private Object optimizersConfig;

    @JsonProperty("quantization_config")
    private Object quantizationConfig;

    @JsonProperty("sparse_vectors")
    private Map<String, Object> sparseVectors;

    @JsonProperty("strict_mode_config")
    private Object strictModeConfig;

    private Map<String, Object> metadata;

    public CreateCollectionRequest() {
    }

    public CreateCollectionRequest(int size, String distance) {
        this.vectors = new VectorsConfig(size, distance);
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

    public Boolean getOnDiskPayload() {
        return onDiskPayload;
    }

    public void setOnDiskPayload(Boolean onDiskPayload) {
        this.onDiskPayload = onDiskPayload;
    }

    public Object getHnswConfig() {
        return hnswConfig;
    }

    public void setHnswConfig(Object hnswConfig) {
        this.hnswConfig = hnswConfig;
    }

    public Object getWalConfig() {
        return walConfig;
    }

    public void setWalConfig(Object walConfig) {
        this.walConfig = walConfig;
    }

    public Object getOptimizersConfig() {
        return optimizersConfig;
    }

    public void setOptimizersConfig(Object optimizersConfig) {
        this.optimizersConfig = optimizersConfig;
    }

    public Object getQuantizationConfig() {
        return quantizationConfig;
    }

    public void setQuantizationConfig(Object quantizationConfig) {
        this.quantizationConfig = quantizationConfig;
    }

    public Map<String, Object> getSparseVectors() {
        return sparseVectors;
    }

    public void setSparseVectors(Map<String, Object> sparseVectors) {
        this.sparseVectors = sparseVectors;
    }

    public Object getStrictModeConfig() {
        return strictModeConfig;
    }

    public void setStrictModeConfig(Object strictModeConfig) {
        this.strictModeConfig = strictModeConfig;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
