package io.quarkiverse.qdrant.runtime.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionConfig {

    private CollectionParams params;

    @JsonProperty("hnsw_config")
    private Object hnswConfig;

    @JsonProperty("optimizer_config")
    private Object optimizerConfig;

    @JsonProperty("wal_config")
    private Object walConfig;

    @JsonProperty("quantization_config")
    private Object quantizationConfig;

    @JsonProperty("strict_mode_config")
    private Object strictModeConfig;

    private Map<String, Object> metadata;

    public CollectionConfig() {
    }

    public CollectionParams getParams() {
        return params;
    }

    public void setParams(CollectionParams params) {
        this.params = params;
    }

    public Object getHnswConfig() {
        return hnswConfig;
    }

    public void setHnswConfig(Object hnswConfig) {
        this.hnswConfig = hnswConfig;
    }

    public Object getOptimizerConfig() {
        return optimizerConfig;
    }

    public void setOptimizerConfig(Object optimizerConfig) {
        this.optimizerConfig = optimizerConfig;
    }

    public Object getWalConfig() {
        return walConfig;
    }

    public void setWalConfig(Object walConfig) {
        this.walConfig = walConfig;
    }

    public Object getQuantizationConfig() {
        return quantizationConfig;
    }

    public void setQuantizationConfig(Object quantizationConfig) {
        this.quantizationConfig = quantizationConfig;
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
