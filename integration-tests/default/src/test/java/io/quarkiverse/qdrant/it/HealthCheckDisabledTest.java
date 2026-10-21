package io.quarkiverse.qdrant.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(HealthCheckDisabledTest.HealthDisabledProfile.class)
public class HealthCheckDisabledTest {

    @Test
    void qdrantCheckIsNotRegistered() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("checks.name", not(hasItem("Qdrant health check")));
    }

    public static class HealthDisabledProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.qdrant.health.enabled", "false");
        }
    }
}
