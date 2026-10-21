package io.quarkiverse.qdrant.deployment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;
import io.quarkiverse.qdrant.runtime.QdrantClientRecorder;
import io.quarkiverse.qdrant.runtime.QdrantConfig;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;
import io.quarkus.tls.deployment.spi.TlsRegistryBuildItem;

class QdrantProcessor {

    private static final String FEATURE = "qdrant";

    private static final Logger LOG = Logger.getLogger(QdrantProcessor.class);

    // Referenced by name because the class is package-private.
    // addHealthCheck verifies it still exists, so a rename or move fails the build.
    static final DotName QDRANT_HEALTH_CHECK = DotName.createSimple("io.quarkiverse.qdrant.runtime.QdrantHealthCheck");

    static final DotName QDRANT_CLIENT_NAME = DotName.createSimple(QdrantClientName.class);
    static final DotName QDRANT_CLIENT = DotName.createSimple(QdrantClient.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ExtensionSslNativeSupportBuildItem enableSsl() {
        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void createClients(
            QdrantClientRecorder recorder,
            ShutdownContextBuildItem shutdown,
            BeanDiscoveryFinishedBuildItem beans,
            CombinedIndexBuildItem index,
            TlsRegistryBuildItem tlsRegistryBuildItem,
            List<RequestedQdrantClientBuildItem> requestedClients,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {

        recorder.setTlsRegistry(tlsRegistryBuildItem.registry());

        Set<String> clientNames = new LinkedHashSet<>();

        for (RequestedQdrantClientBuildItem requested : requestedClients) {
            clientNames.add(requested.getName());
        }

        for (AnnotationInstance annotation : index.getIndex().getAnnotations(QDRANT_CLIENT_NAME)) {
            if (annotation.value() != null) {
                clientNames.add(annotation.value().asString());
            }
        }

        for (InjectionPointInfo injectionPoint : beans.getInjectionPoints()) {
            if (injectionPoint.hasDefaultedQualifier()
                    && injectionPoint.getRequiredType().name().equals(QDRANT_CLIENT)) {
                clientNames.add(QdrantConfig.DEFAULT_CLIENT_NAME);
                break;
            }
        }

        if (clientNames.contains(QdrantConfig.DEFAULT_CLIENT_REPORTED_NAME)) {
            LOG.warnf("Qdrant client '%s' is reported under the same health check data key as the default"
                    + " client, so only one of the two appears in the readiness report. Consider renaming it.",
                    QdrantConfig.DEFAULT_CLIENT_REPORTED_NAME);
        }

        for (String name : clientNames) {
            SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                    .configure(QdrantClient.class)
                    .scope(ApplicationScoped.class)
                    .supplier(recorder.qdrantClientSupplier(name))
                    .setRuntimeInit()
                    .startup()
                    .unremovable();

            if (QdrantConfig.isDefaultClient(name)) {
                configurator.addQualifier().annotation(Default.class).done();
            } else {
                configurator.addQualifier().annotation(QdrantClientName.class).addValue("value", name).done();
            }

            syntheticBeans.produce(configurator.done());
        }

        recorder.cleanup(shutdown);
    }

    // The runtime jar carries a Jandex index, so ARC discovers the check on its own and the bean
    // smallrye-health adds for an enabled HealthBuildItem is redundant. Excluding the type is
    // therefore the only way to keep it out: when the health extension is absent, to avoid a build
    // warning about the interface it implements, and when health.enabled is false, to honour it.
    @BuildStep
    void excludeHealthCheckWhenDisabled(QdrantBuildConfig config,
            Capabilities capabilities,
            BuildProducer<ExcludedTypeBuildItem> excludedTypes) {
        if (!capabilities.isPresent(Capability.SMALLRYE_HEALTH) || !config.healthEnabled()) {
            excludedTypes.produce(new ExcludedTypeBuildItem(QDRANT_HEALTH_CHECK.toString()));
        }
    }

    @BuildStep
    void addHealthCheck(QdrantBuildConfig config,
            CombinedIndexBuildItem index,
            BuildProducer<HealthBuildItem> healthChecks) {
        if (index.getIndex().getClassByName(QDRANT_HEALTH_CHECK) == null) {
            throw new IllegalStateException("Qdrant health check class " + QDRANT_HEALTH_CHECK
                    + " was not found in the index. It is referenced by name, so update that reference if it moved.");
        }
        healthChecks.produce(new HealthBuildItem(QDRANT_HEALTH_CHECK.toString(), config.healthEnabled()));
    }
}
