package io.quarkiverse.qdrant.it.tls;

import org.eclipse.microprofile.config.ConfigProvider;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

abstract class AbstractQdrantTlsTestResource implements QuarkusTestResourceLifecycleManager {

    // The subclasses generate certificates with CN=localhost and no SANs, while the client connects
    // to container.getHost(). Those agree on a local Docker or podman host. On a remote Docker host
    // or Testcontainers Cloud, getHost() is an address the certificate does not cover and hostname
    // verification fails, so the TLS tests only run locally.
    static final DockerImageName QDRANT_IMAGE = DockerImageName
            .parse(ConfigProvider.getConfig().getValue("qdrant.container.image", String.class));
}
