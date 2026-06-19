package io.quarkiverse.qdrant.runtime.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionInfoResponse {

    private CollectionInfo result;

    public CollectionInfoResponse() {
    }

    public CollectionInfo getResult() {
        return result;
    }

    public void setResult(CollectionInfo result) {
        this.result = result;
    }
}
