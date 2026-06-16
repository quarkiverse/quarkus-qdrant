package io.quarkiverse.qdrant.deployment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;
import io.quarkiverse.qdrant.runtime.QdrantClientRecorder;
import io.quarkiverse.qdrant.runtime.QdrantConfig;
import io.quarkiverse.qdrant.runtime.health.QdrantHealthCheck;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

class QdrantProcessor {

    private static final String FEATURE = "qdrant";

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
            List<RequestedQdrantClientBuildItem> requestedClients,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {

        Set<String> clientNames = new LinkedHashSet<>();

        for (RequestedQdrantClientBuildItem requested : requestedClients) {
            clientNames.add(requested.getName());
        }

        for (AnnotationInstance annotation : index.getIndex().getAnnotations(QDRANT_CLIENT_NAME)) {
            if (annotation.value() != null) {
                clientNames.add(annotation.value().asString());
            }
        }

        beans.getInjectionPoints().stream()
                .filter(InjectionPointInfo::hasDefaultedQualifier)
                .filter(ip -> ip.getRequiredType().name().equals(QDRANT_CLIENT))
                .findAny()
                .ifPresent(ip -> clientNames.add(QdrantConfig.DEFAULT_CLIENT_NAME));

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

    @BuildStep
    void addHealthCheck(QdrantBuildConfig config,
            BuildProducer<HealthBuildItem> healthChecks) {
        healthChecks.produce(new HealthBuildItem(
                QdrantHealthCheck.class.getName(),
                config.healthEnabled()));
    }
}
