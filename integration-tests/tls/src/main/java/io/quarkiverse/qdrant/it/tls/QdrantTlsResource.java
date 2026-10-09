package io.quarkiverse.qdrant.it.tls;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import io.quarkiverse.qdrant.runtime.QdrantClient;

@Path("/qdrant")
public class QdrantTlsResource {

    @Inject
    QdrantClient client;

    @GET
    @Path("/scheme")
    public String scheme() {
        return client.getBaseUri().getScheme();
    }

    @GET
    @Path("/collections")
    public List<String> collections() {
        return client.listCollections();
    }
}
