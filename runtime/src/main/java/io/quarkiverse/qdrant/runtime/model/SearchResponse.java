package io.quarkiverse.qdrant.runtime.model;

import java.util.List;

public class SearchResponse {

    private List<ScoredPoint> result;

    public SearchResponse() {
    }

    public List<ScoredPoint> getResult() {
        return result;
    }

    public void setResult(List<ScoredPoint> result) {
        this.result = result;
    }
}
