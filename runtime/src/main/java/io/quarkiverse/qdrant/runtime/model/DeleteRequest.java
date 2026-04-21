package io.quarkiverse.qdrant.runtime.model;

import java.util.List;
import java.util.Map;

public class DeleteRequest {

    private List<String> points;
    private Map<String, Object> filter;

    public DeleteRequest() {
    }

    public List<String> getPoints() {
        return points;
    }

    public void setPoints(List<String> points) {
        this.points = points;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public static DeleteRequest byIds(List<String> ids) {
        DeleteRequest request = new DeleteRequest();
        request.setPoints(ids);
        return request;
    }

    public static DeleteRequest byFilter(Map<String, Object> filter) {
        DeleteRequest request = new DeleteRequest();
        request.setFilter(filter);
        return request;
    }
}
