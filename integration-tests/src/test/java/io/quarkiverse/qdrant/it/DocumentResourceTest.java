package io.quarkiverse.qdrant.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentResourceTest {

    @Test
    @Order(1)
    void testHealthCheck() {
        String body = given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .extract().asString();

        assertThat(body)
                .as("Health check should report UP with Qdrant probe")
                .contains("\"status\": \"UP\"")
                .contains("Qdrant REST Client health check");
    }

    @Test
    @Order(2)
    void testListCollections() {
        List<String> collections = List.of(given()
                .when().get("/documents/collections")
                .then()
                .statusCode(200)
                .extract().as(String[].class));

        assertThat(collections)
                .as("Dev Services should have created both collections")
                .contains("documents", "products");
    }

    @Test
    @Order(3)
    void testIndexAndSearch() {
        String id = given()
                .contentType("application/json")
                .body("{\"text\": \"Quarkus is a Java framework for cloud-native applications\", \"source\": \"guide.pdf\"}")
                .when().post("/documents")
                .then()
                .statusCode(201)
                .extract().asString();

        assertThat(id)
                .as("Indexing a document should return a non-empty ID")
                .isNotEmpty();

        List<Document> results = List.of(given()
                .queryParam("text", "Quarkus is a Java framework for cloud-native applications")
                .when().get("/documents/search")
                .then()
                .statusCode(200)
                .extract().as(Document[].class));

        assertThat(results)
                .as("Search should return at least one result")
                .isNotEmpty()
                .first()
                .satisfies(doc -> {
                    assertThat(doc.text)
                            .as("Result text should match indexed document")
                            .isEqualTo("Quarkus is a Java framework for cloud-native applications");
                    assertThat(doc.source)
                            .as("Result source should match indexed document")
                            .isEqualTo("guide.pdf");
                });

        given()
                .when().delete("/documents/" + id)
                .then()

                .statusCode(204);
    }

    @Test
    @Order(4)
    void testDeleteCollectionAndVerify() {
        given()
                .when().delete("/documents/collections/documents")
                .then()
                .statusCode(204);

        List<String> collections = List.of(given()
                .when().get("/documents/collections")
                .then()
                .statusCode(200)
                .extract().as(String[].class));

        assertThat(collections)
                .as("After deletion, only 'products' collection should remain")
                .contains("products")
                .doesNotContain("documents");
    }
}
