package io.quarkiverse.qdrant.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkiverse.qdrant.runtime.QdrantClient;
import io.quarkiverse.qdrant.runtime.model.PointStruct;
import io.quarkiverse.qdrant.runtime.model.ScoredPoint;

@ApplicationScoped
public class DocumentService {

    private static final String COLLECTION = "documents";

    @Inject
    QdrantClient qdrant;

    public String index(Document document) {
        if (document.id == null) {
            document.id = UUID.randomUUID().toString();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("text", document.text);
        payload.put("source", document.source);

        List<Float> vector = fakeVector(document.text);

        PointStruct point = new PointStruct(document.id, vector, payload);
        qdrant.upsert(COLLECTION).point(point).execute();

        return document.id;
    }

    public List<Document> search(float[] queryVector) {
        List<ScoredPoint> results = qdrant.search(COLLECTION)
                .vector(queryVector)
                .limit(10)
                .withPayload(true)
                .withVector(false)
                .execute();

        List<Document> documents = new ArrayList<>();
        if (results != null) {
            for (ScoredPoint point : results) {
                Document doc = new Document();
                doc.id = point.getId();
                if (point.getPayload() != null) {
                    doc.text = (String) point.getPayload().get("text");
                    doc.source = (String) point.getPayload().get("source");
                }
                documents.add(doc);
            }
        }
        return documents;
    }

    public void delete(String id) {
        qdrant.delete(COLLECTION).byIds(List.of(id)).execute();
    }

    /**
     * Generates a simple deterministic 4-dimension vector from text.
     * This is a fake — real embeddings would come from an embedding model.
     */
    static List<Float> fakeVector(String text) {
        int h = text.hashCode();
        return List.of(
                (h & 0xFF) / 255.0f,
                ((h >> 8) & 0xFF) / 255.0f,
                ((h >> 16) & 0xFF) / 255.0f,
                ((h >> 24) & 0xFF) / 255.0f);
    }
}
