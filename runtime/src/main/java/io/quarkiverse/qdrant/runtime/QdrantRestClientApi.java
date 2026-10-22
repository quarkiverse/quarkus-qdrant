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

import io.quarkiverse.qdrant.runtime.model.CollectionInfoResponse;
import io.quarkiverse.qdrant.runtime.model.CreateCollectionRequest;
import io.quarkiverse.qdrant.runtime.model.DeleteRequest;
import io.quarkiverse.qdrant.runtime.model.ListCollectionsResponse;
import io.quarkiverse.qdrant.runtime.model.SearchRequest;
import io.quarkiverse.qdrant.runtime.model.SearchResponse;
import io.quarkiverse.qdrant.runtime.model.UpsertRequest;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Path("/")
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
                        return "Qdrant error (HTTP " + statusCode + "): " + error.asString();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return "Qdrant error (HTTP " + statusCode + ")";
    }

    @PUT
    @Path("/collections/{collection}")
    void createCollection(@PathParam("collection") String collection, CreateCollectionRequest request);

    @PUT
    @Path("/collections/{collection}/points")
    void upsert(@PathParam("collection") String collection, UpsertRequest request);

    @POST
    @Path("/collections/{collection}/points/delete")
    void delete(@PathParam("collection") String collection, DeleteRequest request);

    @POST
    @Path("/collections/{collection}/points/query")
    SearchResponse search(@PathParam("collection") String collection, SearchRequest request);

    @GET
    @Path("/collections")
    ListCollectionsResponse listCollections();

    @GET
    @Path("/collections/{collection}")
    CollectionInfoResponse getCollectionInfo(@PathParam("collection") String collection);

    @DELETE
    @Path("/collections/{collection}")
    void deleteCollection(@PathParam("collection") String collection);

    @GET
    @Path("/healthz")
    @Produces(MediaType.TEXT_PLAIN)
    String healthz();

    class ObjectMapperHolder {
        static final ObjectMapper MAPPER = JsonMapper.builder().build();
    }
}
