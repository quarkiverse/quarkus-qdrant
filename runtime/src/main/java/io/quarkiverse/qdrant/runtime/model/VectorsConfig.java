package io.quarkiverse.qdrant.runtime.model;

public class VectorsConfig {
    private int size;
    private String distance;

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

}
