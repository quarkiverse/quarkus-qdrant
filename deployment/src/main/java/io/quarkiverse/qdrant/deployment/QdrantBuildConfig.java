package io.quarkiverse.qdrant.deployment;

import static io.quarkus.runtime.annotations.ConfigPhase.BUILD_TIME;

import java.util.Map;
import java.util.OptionalInt;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = BUILD_TIME)
@ConfigMapping(prefix = "quarkus.qdrant")
public interface QdrantBuildConfig {
    /**
     * Whether a health check is published in case the smallrye-health extension is present.
     */
    @WithName("health.enabled")
    @WithDefault("true")
    boolean healthEnabled();

    /**
     * Configuration for DevServices. DevServices allows Quarkus to automatically start a database in dev and test mode.
     */
    DevServicesConfig devservices();

    @ConfigGroup
    interface DevServicesConfig {

        /**
         * Whether Dev Services for Qdrant are enabled or not.
         */
        @WithDefault("true")
        boolean enabled();

        /**
         * Container image for Qdrant.
         */
        @WithDefault("docker.io/qdrant/qdrant:v1.16-unprivileged")
        String qdrantImageName();

        /**
         * Optional fixed port the Qdrant dev service will listen to.
         * If not defined, the port will be chosen randomly.
         */
        OptionalInt port();

        /**
         * Indicates if the Dev Service containers managed by Quarkus for Qdrant are shared.
         */
        @WithDefault("true")
        boolean shared();

        /**
         * Service label to apply to created Dev Services containers.
         */
        @WithDefault("qdrant")
        String serviceName();

        /**
         * Collections to create automatically when the Dev Service starts.
         * The key is the collection name.
         */
        Map<String, CollectionConfig> collections();
    }

    @ConfigGroup
    interface CollectionConfig {

        /**
         * The size of the vectors in this collection.
         */
        @WithDefault("384")
        int vectorSize();

        /**
         * The distance metric to use for this collection.
         * Supported values: Cosine, Euclid, Dot, Manhattan.
         */
        @WithDefault("Cosine")
        String distance();
    }
}
