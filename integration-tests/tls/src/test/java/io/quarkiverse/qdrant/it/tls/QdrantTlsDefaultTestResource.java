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

public class QdrantTlsDefaultTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Path CERTS_DIR = Path.of("target/certs/qdrant-tls-default");
    private static final String API_KEY = "test-secret-key";

    private QdrantContainer container;

    @Override
    public Map<String, String> start() {
        generateCertificates();

        container = new QdrantContainer(
                DockerImageName.parse("docker.io/qdrant/qdrant:v1.18-unprivileged")
                        .asCompatibleSubstituteFor("qdrant/qdrant"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath(CERTS_DIR.resolve("qdrant.crt").toString()), "/certs/cert.pem")
                .withCopyFileToContainer(
                        MountableFile.forHostPath(CERTS_DIR.resolve("qdrant.key").toString()), "/certs/key.pem")
                .withEnv("QDRANT__SERVICE__ENABLE_TLS", "true")
                .withEnv("QDRANT__SERVICE__API_KEY", API_KEY)
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
                "quarkus.qdrant.api-key", API_KEY,
                "quarkus.tls.trust-store.pem.certs", CERTS_DIR.resolve("qdrant.crt").toAbsolutePath().toString());
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }

    private static void generateCertificates() {
        try {
            Files.createDirectories(CERTS_DIR);
            CertificateGenerator generator = new CertificateGenerator(CERTS_DIR, true);
            generator.generate(new CertificateRequest()
                    .withName("qdrant")
                    .withFormats(List.of(Format.PEM))
                    .withCN("localhost")
                    .withDuration(Duration.ofDays(2)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TLS certificates", e);
        }
    }
}
