package io.quarkiverse.qdrant.deployment;

import io.quarkiverse.qdrant.runtime.QdrantRestClientProducer;
import io.quarkiverse.qdrant.runtime.health.QdrantHealthCheck;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

class QdrantProcessor {

    private static final String FEATURE = "qdrant";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem registerProducer() {
        return AdditionalBeanBuildItem.unremovableOf(QdrantRestClientProducer.class);
    }

    @BuildStep
    ExtensionSslNativeSupportBuildItem enableSsl() {
        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    }

    @BuildStep
    void addHealthCheck(QdrantBuildConfig config,
            BuildProducer<HealthBuildItem> healthChecks) {
        healthChecks.produce(new HealthBuildItem(
                QdrantHealthCheck.class.getName(),
                config.healthEnabled()));
    }
}
