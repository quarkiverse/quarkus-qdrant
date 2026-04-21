package io.quarkiverse.qdrant.runtime.health;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.quarkiverse.qdrant.runtime.QdrantConfig;
import io.quarkiverse.qdrant.runtime.QdrantHealthApi;

@Readiness
@ApplicationScoped
public class QdrantHealthCheck implements HealthCheck {

    @Inject
    QdrantConfig config;

    private QdrantHealthApi client;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Qdrant REST Client health check");
        try {
            getClient().healthz();
            builder.up();
        } catch (Exception e) {
            return builder.down().withData("reason", e.getMessage()).build();
        }
        return builder.build();
    }

    private QdrantHealthApi getClient() {
        if (client == null) {
            String scheme = config.useTls() ? "https" : "http";
            URI baseUri = URI.create(scheme + "://" + config.host() + ":" + config.port());
            client = RestClientBuilder.newBuilder()
                    .baseUri(baseUri)
                    .build(QdrantHealthApi.class);
        }
        return client;
    }
}
