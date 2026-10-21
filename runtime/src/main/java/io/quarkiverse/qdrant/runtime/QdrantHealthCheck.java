package io.quarkiverse.qdrant.runtime;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import io.quarkus.arc.InjectableBean;
import io.quarkus.arc.InjectableInstance;
import io.quarkus.arc.InstanceHandle;

@Readiness
@ApplicationScoped
class QdrantHealthCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(QdrantHealthCheck.class);

    @Any
    @Inject
    InjectableInstance<QdrantClient> clients;

    // The check runs on each client's own REST client, so it inherits that client's TLS configuration and API key.
    private final Map<String, QdrantClient> checkedClients = new LinkedHashMap<>();

    @PostConstruct
    void init() {
        for (InstanceHandle<QdrantClient> handle : clients.handles()) {
            if (handle.getBean().isActive()) {
                checkedClients.putIfAbsent(getClientName(handle.getBean()), handle.get());
            }
        }
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Qdrant health check");
        boolean allUp = true;

        for (Map.Entry<String, QdrantClient> entry : checkedClients.entrySet()) {
            String name = QdrantConfig.isDefaultClient(entry.getKey())
                    ? QdrantConfig.DEFAULT_CLIENT_REPORTED_NAME
                    : entry.getKey();
            try {
                entry.getValue().restClient().healthz();
                builder.withData(name, "UP");
            } catch (Exception e) {
                LOG.debugf(e, "Qdrant health check failed for client '%s'", name);
                // The message is null for some exceptions, e.g. one carrying only a cause.
                String reason = e.getMessage() != null ? e.getMessage() : e.toString();
                builder.withData(name, "DOWN - " + reason);
                allUp = false;
            }
        }

        return allUp ? builder.up().build() : builder.down().build();
    }

    private static String getClientName(InjectableBean<?> bean) {
        for (Annotation qualifier : bean.getQualifiers()) {
            if (qualifier instanceof QdrantClientName qdrantClientName) {
                return qdrantClientName.value();
            }
        }
        return QdrantConfig.DEFAULT_CLIENT_NAME;
    }
}
