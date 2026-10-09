package io.quarkiverse.qdrant.runtime.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    private QueryResult result;

    public SearchResponse() {
    }

    public QueryResult getResult() {
        return result;
    }

    public void setResult(QueryResult result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueryResult {

        private List<ScoredPoint> points;

        public QueryResult() {
        }

        public List<ScoredPoint> getPoints() {
            return points;
        }

        public void setPoints(List<ScoredPoint> points) {
            this.points = points;
        }
    }
}
