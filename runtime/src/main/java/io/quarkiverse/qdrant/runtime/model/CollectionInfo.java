package io.quarkiverse.qdrant.runtime.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionInfo {

    private String status;

    @JsonProperty("optimizer_status")
    private Object optimizerStatus;

    @JsonProperty("points_count")
    private Long pointsCount;

    @JsonProperty("indexed_vectors_count")
    private Long indexedVectorsCount;

    @JsonProperty("segments_count")
    private Long segmentsCount;

    private CollectionConfig config;

    @JsonProperty("payload_schema")
    private Map<String, Object> payloadSchema;

    private List<Object> warnings;

    @JsonProperty("update_queue")
    private Object updateQueue;

    public CollectionInfo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getOptimizerStatus() {
        return optimizerStatus;
    }

    public void setOptimizerStatus(Object optimizerStatus) {
        this.optimizerStatus = optimizerStatus;
    }

    public Long getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Long pointsCount) {
        this.pointsCount = pointsCount;
    }

    public Long getIndexedVectorsCount() {
        return indexedVectorsCount;
    }

    public void setIndexedVectorsCount(Long indexedVectorsCount) {
        this.indexedVectorsCount = indexedVectorsCount;
    }

    public Long getSegmentsCount() {
        return segmentsCount;
    }

    public void setSegmentsCount(Long segmentsCount) {
        this.segmentsCount = segmentsCount;
    }

    public CollectionConfig getConfig() {
        return config;
    }

    public void setConfig(CollectionConfig config) {
        this.config = config;
    }

    public Map<String, Object> getPayloadSchema() {
        return payloadSchema;
    }

    public void setPayloadSchema(Map<String, Object> payloadSchema) {
        this.payloadSchema = payloadSchema;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    public Object getUpdateQueue() {
        return updateQueue;
    }

    public void setUpdateQueue(Object updateQueue) {
        this.updateQueue = updateQueue;
    }

    @JsonIgnore
    public String getDistance() {
        return config.getParams().getVectors().getDistance();
    }

    @JsonIgnore
    public int getVectorSize() {
        return config.getParams().getVectors().getSize();
    }
}
