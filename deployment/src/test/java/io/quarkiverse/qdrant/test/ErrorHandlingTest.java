package io.quarkiverse.qdrant.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.qdrant.runtime.QdrantApiException;
import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantException;
import io.quarkiverse.qdrant.runtime.model.PointStruct;
import io.quarkus.test.QuarkusUnitTest;

/**
 * Tests for QdrantException error handling.
 * Covers:
 * - Qdrant HTTP API errors (QdrantApiException with status code and details)
 * - Non-HTTP errors wrapped as QdrantException (validation, connection failures)
 * - Null handling in request/result builders
 * - Runtime error scenarios (wrong dimensions, duplicates, missing criteria)
 * - Recovery after exceptions (client state not corrupted)
 */
@Order(4)
public class ErrorHandlingTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .overrideConfigKey("quarkus.qdrant.devservices.collections.test_col.size", "4")
            .overrideConfigKey("quarkus.qdrant.devservices.collections.test_col.distance", "Cosine");

    @Inject
    QdrantClient client;

    // --- Qdrant HTTP API errors (QdrantApiException) ---

    @Test
    void getCollectionInfoOnNonExistentCollectionThrowsQdrantApiException() {
        assertThatThrownBy(() -> client.getCollectionInfo("nonexistent_collection"))
                .isInstanceOf(QdrantApiException.class)
                .isInstanceOf(QdrantException.class)
                .satisfies(e -> {
                    QdrantApiException qe = (QdrantApiException) e;
                    assertThat(qe.getStatusCode()).isGreaterThan(0);
                    assertThat(qe.getErrorDetails()).isNotNull();
                    assertThat(qe.getMessage()).contains("Qdrant error");
                    assertThat(qe.getMessage()).contains("HTTP");
                });
    }

    @Test
    void searchOnNonExistentCollectionThrowsQdrantApiException() {
        assertThatThrownBy(() -> client.search("nonexistent_collection")
                .vector(new float[] { 1.0f, 2.0f, 3.0f, 4.0f })
                .execute())
                .isInstanceOf(QdrantApiException.class)
                .satisfies(e -> {
                    QdrantApiException qe = (QdrantApiException) e;
                    assertThat(qe.getStatusCode()).isGreaterThan(0);
                    assertThat(qe.getErrorDetails()).isNotNull();
                });
    }

    @Test
    void upsertOnNonExistentCollectionThrowsQdrantApiException() {
        assertThatThrownBy(() -> client.upsert("nonexistent_collection")
                .point(new PointStruct("1", List.of(1.0f, 2.0f, 3.0f, 4.0f), Map.of("key", "value")))
                .execute())
                .isInstanceOf(QdrantApiException.class)
                .satisfies(e -> {
                    QdrantApiException qe = (QdrantApiException) e;
                    assertThat(qe.getStatusCode()).isGreaterThan(0);
                });
    }

    @Test
    void createDuplicateCollectionThrowsQdrantApiException() {
        assertThatThrownBy(() -> client.createCollection("test_col")
                .vectorSize(4)
                .distance("Cosine")
                .execute())
                .isInstanceOf(QdrantApiException.class)
                .satisfies(e -> {
                    QdrantApiException qe = (QdrantApiException) e;
                    assertThat(qe.getStatusCode()).isGreaterThan(0);
                });
    }

    @Test
    void upsertWithWrongVectorDimensionThrowsQdrantApiException() {
        assertThatThrownBy(() -> client.upsert("test_col")
                .point(new PointStruct("1", List.of(1.0f, 2.0f), Map.of()))
                .execute())
                .isInstanceOf(QdrantApiException.class)
                .satisfies(e -> {
                    QdrantApiException qe = (QdrantApiException) e;
                    assertThat(qe.getStatusCode()).isGreaterThan(0);
                });
    }

    // --- Non-HTTP errors (QdrantException, not QdrantApiException) ---

    @Test
    void searchWithNullVectorThrowsQdrantException() {
        assertThatThrownBy(() -> client.search("test_col").execute())
                .isInstanceOf(QdrantException.class);
    }

    @Test
    void deleteWithNoCriteriaThrowsQdrantException() {
        assertThatThrownBy(() -> client.delete("test_col").execute())
                .isInstanceOf(QdrantException.class)
                .isNotInstanceOf(QdrantApiException.class)
                .hasMessageContaining("requires either");
    }

    // --- Multiple consecutive errors ---

    @Test
    void multipleConsecutiveErrorsAllThrowQdrantException() {
        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> client.getCollectionInfo("does_not_exist_" + System.nanoTime()))
                    .isInstanceOf(QdrantApiException.class);
        }
    }

    // --- Recovery after exceptions ---

    @Test
    void clientRecoversAfterGetCollectionInfoError() {
        assertThatThrownBy(() -> client.getCollectionInfo("nonexistent"))
                .isInstanceOf(QdrantException.class);

        assertThatNoException().isThrownBy(() -> client.listCollections());
    }

    @Test
    void clientRecoversAfterSearchError() {
        assertThatThrownBy(() -> client.search("nonexistent")
                .vector(new float[] { 1.0f, 2.0f, 3.0f, 4.0f })
                .execute())
                .isInstanceOf(QdrantException.class);

        assertThatNoException().isThrownBy(() -> client.getCollectionInfo("test_col"));
    }

    @Test
    void clientRecoversAfterUpsertError() {
        assertThatThrownBy(() -> client.upsert("test_col")
                .point(new PointStruct("1", List.of(1.0f, 2.0f), Map.of()))
                .execute())
                .isInstanceOf(QdrantException.class);

        assertThatNoException().isThrownBy(() -> client.listCollections());
    }

    @Test
    void clientRecoversAfterCreateCollectionError() {
        assertThatThrownBy(() -> client.createCollection("test_col")
                .vectorSize(4)
                .execute())
                .isInstanceOf(QdrantException.class);

        assertThatNoException().isThrownBy(() -> client.getCollectionInfo("test_col"));
    }

    // --- Exception hierarchy direct tests ---

    @Test
    void qdrantApiExceptionHasStatusCodeAndDetails() {
        QdrantApiException exception = new QdrantApiException("Qdrant error (HTTP 404): Not found", 404,
                "{\"status\":{\"error\":\"Not found\"}}");

        assertThat(exception).isInstanceOf(QdrantException.class);
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getErrorDetails()).contains("Not found");
        assertThat(exception.getMessage()).contains("404");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void qdrantExceptionWithCauseHasNoCause() {
        RuntimeException cause = new RuntimeException("connection refused");
        QdrantException exception = new QdrantException("Failed to connect", cause);

        assertThat(exception).isNotInstanceOf(QdrantApiException.class);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getMessage()).isEqualTo("Failed to connect");
    }

    @Test
    void qdrantApiExceptionWithNullDetails() {
        QdrantApiException exception = new QdrantApiException("Error", 500, null);

        assertThat(exception.getStatusCode()).isEqualTo(500);
        assertThat(exception.getErrorDetails()).isNull();
    }

    // --- Positive tests: successful operations ---

    @Test
    void listCollectionsSucceeds() {
        List<String> collections = client.listCollections();
        assertThat(collections).isNotNull().contains("test_col");
    }

    @Test
    void getCollectionInfoOnExistingCollectionSucceeds() {
        assertThatNoException().isThrownBy(() -> client.getCollectionInfo("test_col"));
    }
}
