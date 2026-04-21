package io.quarkiverse.qdrant.runtime;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public interface QdrantHealthApi {

    @GET
    @Path("healthz")
    String healthz();
}
