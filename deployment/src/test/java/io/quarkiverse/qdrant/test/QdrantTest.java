package io.quarkiverse.qdrant.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkus.test.QuarkusUnitTest;

public class QdrantTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    QdrantClient defaultClient;

    @Test
    void defaultClientIsCreated() {
        assertNotNull(defaultClient);
    }

    @Test
    void defaultClientUsesConfiguredUri() {
        String uri = defaultClient.getBaseUri().toString();
        assertTrue(uri.startsWith("http://localhost:"), "URI should point to localhost, got: " + uri);
    }
}
