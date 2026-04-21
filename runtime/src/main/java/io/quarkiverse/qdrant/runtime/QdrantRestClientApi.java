package io.quarkiverse.qdrant.runtime;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkiverse.qdrant.runtime.model.CreateCollectionRequest;
import io.quarkiverse.qdrant.runtime.model.DeleteRequest;
import io.quarkiverse.qdrant.runtime.model.ListCollectionsResponse;
import io.quarkiverse.qdrant.runtime.model.SearchRequest;
import io.quarkiverse.qdrant.runtime.model.SearchResponse;
import io.quarkiverse.qdrant.runtime.model.UpsertRequest;

@Path("/collections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QdrantRestClientApi {

    @PUT
    @Path("/{collection}")
    void createCollection(@PathParam("collection") String collection, CreateCollectionRequest request);

    @PUT
    @Path("/{collection}/points")
    void upsert(@PathParam("collection") String collection, UpsertRequest request);

    @POST
    @Path("/{collection}/points/delete")
    void delete(@PathParam("collection") String collection, DeleteRequest request);

    @POST
    @Path("/{collection}/points/search")
    SearchResponse search(@PathParam("collection") String collection, SearchRequest request);

    @GET
    ListCollectionsResponse listCollections();

    @DELETE
    @Path("/{collection}")
    void deleteCollection(@PathParam("collection") String collection);
}
