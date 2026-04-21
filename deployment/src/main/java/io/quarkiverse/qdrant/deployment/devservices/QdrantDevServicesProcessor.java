package io.quarkiverse.qdrant.deployment.devservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jboss.logging.Logger;

import io.quarkiverse.qdrant.deployment.QdrantBuildConfig;
import io.quarkus.deployment.IsProduction;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.builditem.DevServicesComposeProjectBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.builditem.DockerStatusBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;
import io.quarkus.devservices.common.ComposeLocator;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ConfigUtils;

@BuildSteps(onlyIfNot = IsProduction.class, onlyIf = DevServicesConfig.Enabled.class)
public class QdrantDevServicesProcessor {

    private static final Logger LOG = Logger.getLogger(QdrantDevServicesProcessor.class);
    private static final String FEATURE = "qdrant";

    @BuildStep
    public void startQdrantDevServices(
            DockerStatusBuildItem dockerStatusBuildItem,
            LaunchModeBuildItem launchMode,
            QdrantBuildConfig qdrantBuildConfig,
            List<DevServicesSharedNetworkBuildItem> devServicesSharedNetworkBuildItem,
            DevServicesConfig devServicesConfig,
            DevServicesComposeProjectBuildItem composeProjectBuildItem,
            io.quarkus.deployment.annotations.BuildProducer<DevServicesResultBuildItem> devServicesResult) {

        if (!qdrantBuildConfig.devservices().enabled()) {
            LOG.debug("Not starting Dev Services for Qdrant, as it has been disabled in the config.");
            return;
        }

        if (ConfigUtils.isPropertyPresent("quarkus.qdrant.host")) {
            LOG.debug("Not starting Dev Services for Qdrant, as quarkus.qdrant.host is already configured.");
            return;
        }

        if (!dockerStatusBuildItem.isContainerRuntimeAvailable()) {
            LOG.warn("Docker isn't working, please configure the Qdrant server location.");
            return;
        }

        boolean useSharedNetwork = DevServicesSharedNetworkBuildItem.isSharedNetworkRequired(devServicesConfig,
                devServicesSharedNetworkBuildItem);

        String imageName = qdrantBuildConfig.devservices().qdrantImageName();
        String serviceName = qdrantBuildConfig.devservices().serviceName();

        // Try to discover an existing container first
        DevServicesResultBuildItem discovered = discoverRunningService(
                composeProjectBuildItem, qdrantBuildConfig, launchMode.getLaunchMode(), useSharedNetwork);
        if (discovered != null) {
            devServicesResult.produce(discovered);
            return;
        }

        // No existing container found — start a new one
        devServicesResult.produce(DevServicesResultBuildItem.owned()
                .feature(FEATURE)
                .serviceName(serviceName)
                .serviceConfig(qdrantBuildConfig.devservices())
                .startable(() -> {
                    QdrantContainer container = new QdrantContainer(
                            imageName,
                            qdrantBuildConfig.devservices().port(),
                            launchMode.getLaunchMode() == LaunchMode.DEVELOPMENT ? serviceName : null,
                            useSharedNetwork);
                    container.withCollections(qdrantBuildConfig.devservices().collections());
                    devServicesConfig.timeout().ifPresent(container::withStartupTimeout);
                    return container;
                })
                .postStartHook(container -> LOG.info("Dev Services for Qdrant started."))
                .configProvider(configProvider())
                .build());
    }

    private DevServicesResultBuildItem discoverRunningService(
            DevServicesComposeProjectBuildItem composeProjectBuildItem,
            QdrantBuildConfig config,
            LaunchMode launchMode,
            boolean useSharedNetwork) {

        String serviceName = config.devservices().serviceName();
        String imageName = config.devservices().qdrantImageName();

        return QdrantDevServices.LOCATOR
                .locateContainer(serviceName, config.devservices().shared(), launchMode)
                .or(() -> ComposeLocator.locateContainer(composeProjectBuildItem,
                        List.of(imageName, "qdrant/qdrant"),
                        QdrantDevServices.QDRANT_PORT, launchMode, useSharedNetwork))
                .map(containerAddress -> DevServicesResultBuildItem.discovered()
                        .feature(FEATURE)
                        .containerId(containerAddress.getId())
                        .config(Map.of(
                                "quarkus.qdrant.host", containerAddress.getHost(),
                                "quarkus.qdrant.port", String.valueOf(containerAddress.getPort())))
                        .build())
                .orElse(null);
    }

    private static Map<String, Function<QdrantContainer, String>> configProvider() {
        Map<String, Function<QdrantContainer, String>> config = new HashMap<>();
        config.put("quarkus.qdrant.host", QdrantContainer::getHost);
        config.put("quarkus.qdrant.port", c -> String.valueOf(c.getPort()));
        return config;
    }
}
