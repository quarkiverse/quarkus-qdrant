package io.quarkiverse.qdrant.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;

@Order(99)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QdrantDevModeTest {

    @RegisterExtension
    static final QuarkusDevModeTest devModeTest = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(DevModeResource.class)
                    .add(new StringAsset(
                            "quarkus.qdrant.devservices.collections.devmode_col.vector-size=4\n"
                                    + "quarkus.qdrant.devservices.collections.devmode_col.distance=Cosine\n"),
                            "application.properties"));

    @Test
    @Order(1)
    void qdrantClientWorksInDevMode() {
        given()
                .when().get("/devmode/collections")
                .then()
                .statusCode(200)
                .body("$", hasItem("devmode_col"));
    }

    @Test
    @Order(2)
    void hotReloadPreservesQdrantClientFunctionality() {
        given()
                .when().get("/devmode/greeting")
                .then()
                .statusCode(200)
                .body(is("hello"));

        devModeTest.modifySourceFile(DevModeResource.class,
                s -> s.replace("\"hello\"", "\"hello-reloaded\""));

        given()
                .when().get("/devmode/greeting")
                .then()
                .statusCode(200)
                .body(is("hello-reloaded"));

        given()
                .when().get("/devmode/collections")
                .then()
                .statusCode(200)
                .body("$", hasItem("devmode_col"));
    }

    @Test
    @Order(3)
    void configChangePreservesQdrantClientFunctionality() {
        devModeTest.modifyResourceFile("application.properties",
                s -> s + "quarkus.qdrant.devservices.collections.extra_col.vector-size=4\n"
                        + "quarkus.qdrant.devservices.collections.extra_col.distance=Cosine\n");

        given()
                .when().get("/devmode/collections")
                .then()
                .statusCode(200)
                .body("$", hasItem("devmode_col"))
                .body("$", hasItem("extra_col"));
    }
}
