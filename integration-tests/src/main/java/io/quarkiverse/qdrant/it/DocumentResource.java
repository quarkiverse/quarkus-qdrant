/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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

    @GET
    @Path("/collections")
    public List<String> listCollections() {
        return qdrant.listCollections();
    }

    @DELETE
    @Path("/collections/{name}")
    public Response deleteCollection(@PathParam("name") String name) {
        qdrant.deleteCollection(name);
        return Response.noContent().build();
    }
}
