package io.quarkiverse.qdrant.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;
import io.quarkus.test.QuarkusUnitTest;

public class NamedOnlyQdrantClientTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(NamedOnlyConsumer.class))
            .overrideConfigKey("quarkus.qdrant.\"secondary\".host", "qdrant-secondary")
            .overrideConfigKey("quarkus.qdrant.\"secondary\".port", "6334");

    @Inject
    @QdrantClientName("secondary")
    QdrantClient secondaryClient;

    @Any
    @Inject
    Instance<QdrantClient> allClients;

    @Test
    void namedClientIsCreated() {
        assertNotNull(secondaryClient);
        assertEquals("http://qdrant-secondary:6334", secondaryClient.getBaseUri().toString());
    }

    @Test
    void noDefaultClientIsCreated() {
        long count = allClients.stream().count();
        assertTrue(count == 1, "Only the named client should exist, but found " + count + " client(s)");
    }

    @ApplicationScoped
    public static class NamedOnlyConsumer {

        @Inject
        @QdrantClientName("secondary")
        QdrantClient client;
    }
}
