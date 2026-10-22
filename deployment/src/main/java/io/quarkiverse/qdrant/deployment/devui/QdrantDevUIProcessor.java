package io.quarkiverse.qdrant.deployment.devui;

import io.quarkiverse.qdrant.runtime.devui.QdrantDevUIService;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devjsonrpc.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

public class QdrantDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    CardPageBuildItem createCard() {
        CardPageBuildItem card = new CardPageBuildItem();

        card.addPage(Page.externalPageBuilder("Qdrant Dashboard")
                .dynamicUrlJsonRPCMethodName("getDashboardLink")
                .doNotEmbed()
                .icon("font-awesome-solid:database"));

        return card;
    }

    @BuildStep
    JsonRPCProvidersBuildItem createJsonRPCService() {
        return new JsonRPCProvidersBuildItem(QdrantDevUIService.class);
    }
}
