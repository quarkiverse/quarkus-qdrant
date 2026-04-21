package io.quarkiverse.qdrant.deployment.devservices;

import java.time.Duration;
import java.util.Map;
import java.util.OptionalInt;

import org.jboss.logging.Logger;
import org.testcontainers.utility.DockerImageName;

import io.quarkiverse.qdrant.deployment.QdrantBuildConfig;
import io.quarkus.devservices.common.ConfigureUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

public class QdrantContainer extends org.testcontainers.qdrant.QdrantContainer
        implements io.quarkus.deployment.builditem.Startable {

    private static final Logger LOG = Logger.getLogger(QdrantContainer.class);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final OptionalInt fixedExposedPort;
    private final boolean useSharedNetwork;
    private String hostName = null;
    private Map<String, QdrantBuildConfig.CollectionConfig> collections = Map.of();

    public QdrantContainer(
            String image,
            OptionalInt fixedExposedPort,
            String serviceName,
            boolean useSharedNetwork) {

        super(DockerImageName.parse(image).asCompatibleSubstituteFor("qdrant/qdrant"));

        if (serviceName != null) {
            withLabel(QdrantDevServices.DEV_SERVICE_LABEL, serviceName);
        }

        this.fixedExposedPort = fixedExposedPort;
        this.useSharedNetwork = useSharedNetwork;
    }

    public QdrantContainer withCollections(Map<String, QdrantBuildConfig.CollectionConfig> collections) {
        this.collections = collections;
        return this;
    }

    @Override
    public void start() {
        super.start();
        createCollections();
    }

    private void createCollections() {
        if (collections.isEmpty()) {
            return;
        }

        // Use the external host/port (not shared network) since we're calling from the build JVM
        String host = super.getHost();
        int port = getMappedPort(QdrantDevServices.QDRANT_PORT);

        LOG.infof("Creating %d Qdrant collection(s) via Dev Services", collections.size());

        Vertx vertx = Vertx.vertx();
        try {
            WebClient client = WebClient.create(new io.vertx.mutiny.core.Vertx(vertx));
            try {
                collections.forEach((name, config) -> createCollection(client, host, port, name, config));
            } finally {
                client.close();
            }
        } finally {
            vertx.close();
        }
    }

    private void createCollection(WebClient client, String host, int port, String name,
            QdrantBuildConfig.CollectionConfig config) {
        JsonObject body = new JsonObject()
                .put("vectors", new JsonObject()
                        .put("size", config.vectorSize())
                        .put("distance", config.distance()));

        try {
            HttpResponse<Buffer> response = client.put(port, host, "/collections/" + name)
                    .putHeader("Content-Type", "application/json")
                    .sendBuffer(Buffer.buffer(body.encode()))
                    .await().atMost(REQUEST_TIMEOUT);

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOG.infof("Created Qdrant collection '%s' (size=%d, distance=%s)",
                        name, config.vectorSize(), config.distance());
            } else {
                LOG.errorf("Failed to create Qdrant collection '%s': HTTP %d", name, response.statusCode());
            }
        } catch (Exception e) {
            LOG.errorf(e, "Failed to create Qdrant collection '%s'", name);
        }
    }

    @Override
    protected void configure() {
        super.configure();

        if (useSharedNetwork) {
            hostName = ConfigureUtil.configureSharedNetwork(this, "qdrant");
            return;
        }

        if (fixedExposedPort.isPresent()) {
            addFixedExposedPort(fixedExposedPort.getAsInt(), QdrantDevServices.QDRANT_PORT);
        } else {
            addExposedPort(QdrantDevServices.QDRANT_PORT);
        }
    }

    public int getPort() {
        if (useSharedNetwork) {
            return QdrantDevServices.QDRANT_PORT;
        }

        if (fixedExposedPort.isPresent()) {
            return fixedExposedPort.getAsInt();
        }

        return super.getMappedPort(QdrantDevServices.QDRANT_PORT);
    }

    @Override
    public String getHost() {
        return useSharedNetwork ? hostName : super.getHost();
    }

    @Override
    public String getConnectionInfo() {
        return getHost() + ":" + getPort();
    }

    @Override
    public void close() {
        super.close();
    }
}
