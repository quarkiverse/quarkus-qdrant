package io.quarkiverse.qdrant.it.tls;

import org.eclipse.microprofile.config.ConfigProvider;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

abstract class AbstractQdrantTlsTestResource implements QuarkusTestResourceLifecycleManager {

    static final DockerImageName QDRANT_IMAGE = DockerImageName
            .parse(ConfigProvider.getConfig().getValue("qdrant.container.image", String.class));
}
