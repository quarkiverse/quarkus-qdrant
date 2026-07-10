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
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.qdrant.runtime.model.CollectionInfoResponse;
import io.quarkiverse.qdrant.runtime.model.CreateCollectionRequest;
import io.quarkiverse.qdrant.runtime.model.DeleteRequest;
import io.quarkiverse.qdrant.runtime.model.ListCollectionsResponse;
import io.quarkiverse.qdrant.runtime.model.SearchRequest;
import io.quarkiverse.qdrant.runtime.model.SearchResponse;
import io.quarkiverse.qdrant.runtime.model.UpsertRequest;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;

@Path("/collections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QdrantRestClientApi {

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            int statusCode = response.getStatus();
            String body = response.readEntity(String.class);
            String errorMessage = extractErrorMessage(statusCode, body);
            return new QdrantApiException(errorMessage, statusCode, body);
        }
        return null;
    }

    private static String extractErrorMessage(int statusCode, String body) {
        if (body != null && !body.isBlank()) {
            try {
                JsonNode root = ObjectMapperHolder.MAPPER.readTree(body);
                JsonNode status = root.get("status");
                if (status != null && status.isObject()) {
                    JsonNode error = status.get("error");
                    if (error != null) {
                        return "Qdrant error (HTTP " + statusCode + "): " + error.asText();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return "Qdrant error (HTTP " + statusCode + ")";
    }

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

    @GET
    @Path("/{collection}")
    CollectionInfoResponse getCollectionInfo(@PathParam("collection") String collection);

    @DELETE
    @Path("/{collection}")
    void deleteCollection(@PathParam("collection") String collection);

    class ObjectMapperHolder {
        static final ObjectMapper MAPPER = new ObjectMapper();
    }
}
