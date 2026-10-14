package io.quarkiverse.qdrant.it;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import io.quarkiverse.qdrant.runtime.QdrantApiException;
import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.QdrantClientName;

/**
 * Resource used for name client tests.
 */
@Path("/admin")
@ApplicationScoped
public class AdminResource {

    @Inject
    @QdrantClientName("admin")
    QdrantClient secondaryClient;

    @PUT
    @Path("/collections/{name}")
    public Response createCollection(@PathParam("name") String name,
            @QueryParam("vectorSize") @DefaultValue("4") int vectorSize,
            @QueryParam("distance") @DefaultValue("Cosine") String distance) {
        try {
            secondaryClient.createCollection(name)
                    .vectorSize(vectorSize)
                    .distance(distance)
                    .execute();
            return Response.status(201).build();
        } catch (QdrantApiException e) {
            return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/collections")
    public List<String> listCollections() {
        return secondaryClient.listCollections();
    }

    @GET
    @Path("/collections/{name}")
    public Response getCollectionInfo(@PathParam("name") String name) {
        try {
            return Response.ok(secondaryClient.getCollectionInfo(name)).build();
        } catch (QdrantApiException e) {
            return Response.status(e.getStatusCode()).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/collections/{name}")
    public Response deleteCollection(@PathParam("name") String name) {
        secondaryClient.deleteCollection(name);
        return Response.noContent().build();
    }

}
