package io.quarkiverse.qdrant.it;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import io.quarkiverse.qdrant.runtime.QdrantClient;

@Path("/documents")
@ApplicationScoped
public class DocumentResource {

    @Inject
    DocumentService service;

    @Inject
    QdrantClient qdrant;

    @POST
    public Response index(Document document) {
        String id = service.index(document);
        return Response.status(201).entity(id).build();
    }

    @GET
    @Path("/search")
    public List<Document> search(@QueryParam("text") String text) {
        List<Float> vector = DocumentService.fakeVector(text);
        float[] array = new float[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            array[i] = vector.get(i);
        }
        return service.search(array);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        service.delete(id);
        return Response.noContent().build();
    }

}
