package io.quarkiverse.qdrant.it;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;

@Path("/named-client")
@ApplicationScoped
public class NamedClientResource {

    @Inject
    @QdrantClientName("secondary")
    QdrantClient secondaryClient;

    @GET
    @Path("/collections")
    public List<String> listCollections() {
        return secondaryClient.listCollections();
    }
}
