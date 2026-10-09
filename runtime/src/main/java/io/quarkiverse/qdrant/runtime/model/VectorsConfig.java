package io.quarkiverse.qdrant.runtime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VectorsConfig {
    private int size;
    private String distance;
    private String memory;

    public VectorsConfig() {
    }

    public VectorsConfig(int size, String distance) {
        this.size = size;
        this.distance = distance;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }
}
