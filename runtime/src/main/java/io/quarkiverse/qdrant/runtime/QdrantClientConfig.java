package io.quarkiverse.qdrant.runtime;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface QdrantClientConfig {

    /**
     * The hostname of the Qdrant server.
     */
    @WithDefault("localhost")
    String host();

    /**
     * The REST port of the Qdrant server.
     */
    @WithDefault("6333")
    int port();

    /**
     * The API key to authenticate with.
     */
    Optional<String> apiKey();

    /**
     * Whether to use TLS (HTTPS).
     */
    @WithDefault("false")
    boolean useTls();
}
