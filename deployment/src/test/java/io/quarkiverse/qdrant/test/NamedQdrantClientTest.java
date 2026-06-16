package io.quarkiverse.qdrant.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;
import io.quarkus.test.QuarkusUnitTest;

public class NamedQdrantClientTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(NamedClientConsumer.class))
            .overrideConfigKey("quarkus.qdrant.\"secondary\".host", "qdrant-secondary")
            .overrideConfigKey("quarkus.qdrant.\"secondary\".port", "6334");

    @Inject
    QdrantClient defaultClient;

    @Inject
    @QdrantClientName("secondary")
    QdrantClient secondaryClient;

    @Test
    void defaultClientIsCreated() {
        assertNotNull(defaultClient);
    }

    @Test
    void namedClientIsCreated() {
        assertNotNull(secondaryClient);
    }

    @Test
    void namedClientUsesItsOwnConfig() {
        assertEquals("http://qdrant-secondary:6334", secondaryClient.getBaseUri().toString());
    }

    @Test
    void defaultAndNamedClientsAreDifferentInstances() {
        assertNotSame(defaultClient, secondaryClient);
    }

    @ApplicationScoped
    public static class NamedClientConsumer {

        @Inject
        @QdrantClientName("secondary")
        QdrantClient client;
    }
}
