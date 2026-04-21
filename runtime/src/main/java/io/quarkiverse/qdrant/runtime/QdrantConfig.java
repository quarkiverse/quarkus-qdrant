package io.quarkiverse.qdrant.runtime;

import static io.quarkus.runtime.annotations.ConfigPhase.RUN_TIME;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = RUN_TIME)
@ConfigMapping(prefix = "quarkus.qdrant")
public interface QdrantConfig {

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
