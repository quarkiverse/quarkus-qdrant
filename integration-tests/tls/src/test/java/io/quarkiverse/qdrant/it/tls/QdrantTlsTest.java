package io.quarkiverse.qdrant.it.tls;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithTestResource(QdrantTlsTestResource.class)
public class QdrantTlsTest {

    @Test
    void clientConnectsOverTls() {
        given()
                .when().get("/qdrant/scheme")
                .then()
                .statusCode(200)
                .body(is("https"));
    }

    @Test
    void clientOperatesOverTls() {
        given()
                .when().get("/qdrant/collections")
                .then()
                .statusCode(200);
    }

    @Test
    void healthCheckIsUpOverTls() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("checks.name", hasItem("Qdrant health check"))
                .body("status", is("UP"))
                .body("checks.find { it.name == 'Qdrant health check' }.status", is("UP"))
                .body("checks.find { it.name == 'Qdrant health check' }.data.'default'", is("UP"))
                .body("checks.find { it.name == 'Qdrant health check' }.data.size()", is(1));
    }
}
