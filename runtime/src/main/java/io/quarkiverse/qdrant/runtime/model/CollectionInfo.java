package io.quarkiverse.qdrant.runtime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CollectionInfo {

    private String status;

    @JsonProperty("points_count")
    private Long pointsCount;

    @JsonProperty("indexed_vectors_count")
    private Long indexedVectorsCount;

    private CollectionConfig config;

    public CollectionInfo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public CollectionConfig getConfig() {
        return config;
    }

    public void setConfig(CollectionConfig config) {
        this.config = config;
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
