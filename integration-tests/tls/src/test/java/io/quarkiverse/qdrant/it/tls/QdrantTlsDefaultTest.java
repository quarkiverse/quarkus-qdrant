package io.quarkiverse.qdrant.it.tls;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithTestResource(QdrantTlsDefaultTestResource.class)
public class QdrantTlsDefaultTest {

    @Test
    void clientConnectsOverTlsWithDefaultConfig() {
        given()
                .when().get("/qdrant/scheme")
                .then()
                .statusCode(200)
                .body(is("https"));
    }

    @Test
    void clientOperatesOverTlsWithDefaultConfig() {
        given()
                .when().get("/qdrant/collections")
                .then()
                .statusCode(200);
    }
}
