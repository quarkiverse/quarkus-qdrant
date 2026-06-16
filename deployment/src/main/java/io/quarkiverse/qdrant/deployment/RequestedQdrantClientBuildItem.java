package io.quarkiverse.qdrant.deployment;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * Build item that other extensions can produce to request a named Qdrant client.
 * <p>
 * For example, {@code quarkus-langchain4j-qdrant} can request a client for each
 * named embedding store it needs to connect to.
 */
public final class RequestedQdrantClientBuildItem extends MultiBuildItem {

    private final String name;

    public RequestedQdrantClientBuildItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
