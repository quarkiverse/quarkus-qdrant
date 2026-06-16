package io.quarkiverse.qdrant.runtime.health;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;
import io.quarkiverse.qdrant.runtime.QdrantHealthApi;

@Readiness
@ApplicationScoped
public class QdrantHealthCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(QdrantHealthCheck.class);

    @Any
    @Inject
    Instance<QdrantClient> clients;

    // Separate from QdrantRestClientApi: healthz is an infrastructure endpoint, not part of the data API exposed by QdrantClient.
    private final Map<String, QdrantHealthApi> healthApis = new HashMap<>();

    @PostConstruct
    void init() {
        for (Instance.Handle<QdrantClient> handle : clients.handles()) {
            String name = "default";
            for (Annotation qualifier : handle.getBean().getQualifiers()) {
                if (qualifier instanceof QdrantClientName qcn) {
                    name = qcn.value();
                }
            }
            healthApis.put(name, RestClientBuilder.newBuilder()
                    .baseUri(handle.get().getBaseUri())
                    .build(QdrantHealthApi.class));
        }
    }

    @PreDestroy
    void cleanup() {
        for (QdrantHealthApi api : healthApis.values()) {
            if (api instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    LOG.warn("Failed to close Qdrant health API client: " + e.getMessage());
                }
            }
        }
        healthApis.clear();
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Qdrant health check");
        boolean allUp = true;

        for (Map.Entry<String, QdrantHealthApi> entry : healthApis.entrySet()) {
            String name = entry.getKey();
            try {
                entry.getValue().healthz();
                builder.withData(name, "UP");
            } catch (Exception e) {
                builder.withData(name, "DOWN - " + e.getMessage());
                allUp = false;
            }
        }

        return allUp ? builder.up().build() : builder.down().build();
    }
}
