package io.quarkiverse.qdrant.it.tls;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.qdrant.QdrantContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.certs.CertificateGenerator;
import io.smallrye.certs.CertificateRequest;
import io.smallrye.certs.Format;

public class QdrantTlsWrongTrustStoreTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Path SERVER_CERTS_DIR = Path.of("target/certs/qdrant-tls-server");
    private static final Path WRONG_CERTS_DIR = Path.of("target/certs/qdrant-tls-wrong");

    private QdrantContainer container;

    @Override
    public Map<String, String> start() {
        generateCertificates(SERVER_CERTS_DIR, "server");
        generateCertificates(WRONG_CERTS_DIR, "wrong");

        container = new QdrantContainer(
                DockerImageName.parse("docker.io/qdrant/qdrant:v1.18-unprivileged")
                        .asCompatibleSubstituteFor("qdrant/qdrant"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath(SERVER_CERTS_DIR.resolve("server.crt").toString()), "/certs/cert.pem")
                .withCopyFileToContainer(
                        MountableFile.forHostPath(SERVER_CERTS_DIR.resolve("server.key").toString()), "/certs/key.pem")
                .withEnv("QDRANT__SERVICE__ENABLE_TLS", "true")
                .withEnv("QDRANT__TLS__CERT", "/certs/cert.pem")
                .withEnv("QDRANT__TLS__KEY", "/certs/key.pem");

        container.setWaitStrategy(Wait.forHttp("/healthz")
                .forPort(6333)
                .usingTls()
                .allowInsecure());

        container.start();

        return Map.of(
                "quarkus.qdrant.devservices.enabled", "false",
                "quarkus.qdrant.host", container.getHost(),
                "quarkus.qdrant.port", String.valueOf(container.getMappedPort(6333)),
                "quarkus.qdrant.use-tls", "true",
                "quarkus.qdrant.tls-configuration-name", "qdrant-wrong",
                "quarkus.tls.qdrant-wrong.trust-store.pem.certs",
                WRONG_CERTS_DIR.resolve("wrong.crt").toAbsolutePath().toString());
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }

    private static void generateCertificates(Path certsDir, String name) {
        try {
            Files.createDirectories(certsDir);
            CertificateGenerator generator = new CertificateGenerator(certsDir, true);
            generator.generate(new CertificateRequest()
                    .withName(name)
                    .withFormats(List.of(Format.PEM))
                    .withCN("localhost")
                    .withDuration(Duration.ofDays(2)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TLS certificates", e);
        }
    }
}
