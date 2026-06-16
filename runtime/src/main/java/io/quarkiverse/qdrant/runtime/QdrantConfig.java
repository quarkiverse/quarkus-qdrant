package io.quarkiverse.qdrant.runtime;

import static io.quarkus.runtime.annotations.ConfigPhase.RUN_TIME;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefaults;
import io.smallrye.config.WithParentName;
import io.smallrye.config.WithUnnamedKey;

@ConfigRoot(phase = RUN_TIME)
@ConfigMapping(prefix = "quarkus.qdrant")
public interface QdrantConfig {

    String DEFAULT_CLIENT_NAME = "<default>";

    /**
     * Qdrant client configurations.
     * <p>
     * The default client uses bare properties (e.g. {@code quarkus.qdrant.host}).
     * Named clients use a quoted key (e.g. {@code quarkus.qdrant."secondary".host}).
     */
    @WithParentName
    @WithDefaults
    @WithUnnamedKey(DEFAULT_CLIENT_NAME)
    @ConfigDocMapKey("qdrant-client-name")
    Map<String, QdrantClientConfig> clients();

    static boolean isDefaultClient(String name) {
        return DEFAULT_CLIENT_NAME.equals(name);
    }
}
