package io.quarkiverse.qdrant.runtime;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

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

                RestClientBuilder builder = RestClientBuilder.newBuilder()
                        .baseUri(baseUri);

                clientConfig.apiKey().ifPresent(key -> builder.header("api-key", key));

                QdrantRestClientApi restClient = builder.build(QdrantRestClientApi.class);
                restClients.put(clientName, restClient);
                return new QdrantClient(restClient, baseUri);
            }
        };
    }

    public void cleanup(ShutdownContext context) {
        context.addShutdownTask(() -> {
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
        });
    }
}
