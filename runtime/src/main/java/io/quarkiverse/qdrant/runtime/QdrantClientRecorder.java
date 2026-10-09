package io.quarkiverse.qdrant.runtime;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.logging.Logger;

import io.quarkus.arc.Arc;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.tls.TlsConfiguration;
import io.quarkus.tls.TlsConfigurationRegistry;

@Recorder
public class QdrantClientRecorder {

    private static final Logger LOG = Logger.getLogger(QdrantClientRecorder.class);

    private final RuntimeValue<QdrantConfig> config;

    private final Map<String, QdrantRestClientApi> restClients = new ConcurrentHashMap<>();

    public QdrantClientRecorder(RuntimeValue<QdrantConfig> config) {
        this.config = config;
    }

    public Supplier<QdrantClient> qdrantClientSupplier(String clientName) {
        return new Supplier<>() {
            @Override
            public QdrantClient get() {
                QdrantClientConfig clientConfig = config.getValue().clients().get(clientName);
                if (clientConfig == null) {
                    throw new IllegalStateException(
                            "No configuration found for Qdrant client '" + clientName
                                    + "'. Add quarkus.qdrant.\"" + clientName + "\".host=... to your configuration.");
                }

                String scheme = clientConfig.useTls() ? "https" : "http";
                URI baseUri = URI.create(scheme + "://" + clientConfig.host() + ":" + clientConfig.port());

                QuarkusRestClientBuilder builder = QuarkusRestClientBuilder.newBuilder()
                        .baseUri(baseUri);

                if (clientConfig.apiKey().isPresent()) {
                    String apiKey = clientConfig.apiKey().get();
                    builder.register((ClientRequestFilter) ctx -> ctx.getHeaders().putSingle("api-key", apiKey));
                }

                if (clientConfig.useTls()) {
                    configureTls(builder, clientConfig);
                }

                QdrantRestClientApi restClient = builder.build(QdrantRestClientApi.class);
                restClients.put(clientName, restClient);
                return new QdrantClient(restClient, baseUri);
            }
        };
    }

    private void configureTls(QuarkusRestClientBuilder builder, QdrantClientConfig clientConfig) {
        TlsConfigurationRegistry registry = Arc.container().select(TlsConfigurationRegistry.class).orNull();
        if (registry == null) {
            if (clientConfig.tlsConfigurationName().isPresent()) {
                throw new IllegalStateException(
                        "TLS configuration '" + clientConfig.tlsConfigurationName().get()
                                + "' was specified, but no TLS configuration registry is available.");
            }
            return;
        }

        Optional<TlsConfiguration> tlsConfig;
        if (clientConfig.tlsConfigurationName().isPresent()) {
            tlsConfig = TlsConfiguration.from(registry, clientConfig.tlsConfigurationName());
            if (tlsConfig.isEmpty()) {
                throw new IllegalStateException(
                        "TLS configuration '" + clientConfig.tlsConfigurationName().get()
                                + "' was specified, but it does not exist.");
            }
        } else {
            tlsConfig = registry.getDefault();
        }

        tlsConfig.ifPresent(builder::tlsConfiguration);
    }

    public void cleanup(ShutdownContext context) {
        context.addShutdownTask(new ShutdownTask(restClients));
    }

    private static final class ShutdownTask implements Runnable {

        private static final Logger LOG = Logger.getLogger(ShutdownTask.class);

        private final Map<String, QdrantRestClientApi> restClients;

        ShutdownTask(Map<String, QdrantRestClientApi> restClients) {
            this.restClients = restClients;
        }

        @Override
        public void run() {
            for (QdrantRestClientApi restClient : restClients.values()) {
                if (restClient instanceof AutoCloseable closeable) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        LOG.warn("Failed to close Qdrant REST client: " + e.getMessage());
                    }
                }
            }
            restClients.clear();
        }
    }
}
