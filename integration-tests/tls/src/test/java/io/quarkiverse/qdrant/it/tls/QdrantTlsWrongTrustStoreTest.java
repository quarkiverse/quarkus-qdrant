package io.quarkiverse.qdrant.it.tls;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithTestResource(QdrantTlsWrongTrustStoreTestResource.class)
public class QdrantTlsWrongTrustStoreTest {

    @Test
    void connectionFailsWithWrongTrustStore() {
        given()
                .when().get("/qdrant/collections")
                .then()
                .statusCode(500);
    }
}
