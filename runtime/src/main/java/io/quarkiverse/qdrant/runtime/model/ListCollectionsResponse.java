package io.quarkiverse.qdrant.runtime.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListCollectionsResponse {

    private Result result;

    public ListCollectionsResponse() {
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        private List<CollectionDescription> collections;

        public Result() {
        }

        public List<CollectionDescription> getCollections() {
            return collections;
        }

        public void setCollections(List<CollectionDescription> collections) {
            this.collections = collections;
        }
    }
}
