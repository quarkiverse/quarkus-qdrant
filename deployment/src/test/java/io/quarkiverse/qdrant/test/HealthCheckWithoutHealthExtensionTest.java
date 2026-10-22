package io.quarkiverse.qdrant.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.logging.LogRecord;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkus.test.QuarkusUnitTest;

/**
 * The deployment module has no smallrye-health on its classpath, so this test covers an application using
 * the extension without the health extension.
 * <p>
 * The runtime jar carries a Jandex index, so without the exclusion produced by
 * {@code QdrantProcessor#excludeHealthCheckWhenDisabled} ARC discovers the check on its own: arc-processor
 * logs "Failed to index" for the MicroProfile Health interface it implements during augmentation, and ARC
 * logs "Unable to load removed bean type" at startup. Both warnings were reported in #81.
 */
@Order(5)
public class HealthCheckWithoutHealthExtensionTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .overrideConfigKey("quarkus.qdrant.devservices.enabled", "false")
            .setLogRecordPredicate(HealthCheckWithoutHealthExtensionTest::isBeanDiscoveryWarning)
            .assertLogRecords(records -> assertThat(records)
                    .extracting(LogRecord::getMessage)
                    .as("the health check must stay out of bean discovery when smallrye-health is absent")
                    .isEmpty());

    @Inject
    QdrantClient client;

    @Test
    void clientIsCreatedWithoutTheHealthExtension() {
        assertThat(client).isNotNull();
    }

    private static boolean isBeanDiscoveryWarning(LogRecord record) {
        String message = record.getMessage();
        return message != null
                && (message.startsWith("Failed to index")
                        || message.startsWith("Unable to load removed bean type"));
    }
}
