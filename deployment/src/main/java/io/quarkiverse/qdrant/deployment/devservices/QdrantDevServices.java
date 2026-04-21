package io.quarkiverse.qdrant.deployment.devservices;

import io.quarkus.devservices.common.ContainerLocator;

public final class QdrantDevServices {
    /**
     * Label to add to shared Dev Service for Qdrant running in containers.
     * This allows other applications to discover the running service and use it instead of starting a new instance.
     */
    static final String DEV_SERVICE_LABEL = "quarkus-dev-service-qdrant";
    static final int QDRANT_PORT = 6333;

    static final ContainerLocator LOCATOR = new ContainerLocator(DEV_SERVICE_LABEL, QDRANT_PORT);

    private QdrantDevServices() {
    }
}
