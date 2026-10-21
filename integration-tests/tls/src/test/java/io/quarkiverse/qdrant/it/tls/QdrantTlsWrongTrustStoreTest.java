package io.quarkiverse.qdrant.it.tls;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

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

    @Test
    void healthCheckIsDownWithWrongTrustStore() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(503)
                .body("checks.name", hasItem("Qdrant health check"))
                .body("status", is("DOWN"))
                .body("checks.find { it.name == 'Qdrant health check' }.status", is("DOWN"))
                .body("checks.find { it.name == 'Qdrant health check' }.data.'default'", startsWith("DOWN - "))
                .body("checks.find { it.name == 'Qdrant health check' }.data.'default'", containsString("SSL"))
                .body("checks.find { it.name == 'Qdrant health check' }.data.size()", is(1));
    }
}
