package io.quarkiverse.qdrant.runtime.model;

import java.util.List;

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
