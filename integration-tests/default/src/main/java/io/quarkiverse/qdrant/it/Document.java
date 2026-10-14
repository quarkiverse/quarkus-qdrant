package io.quarkiverse.qdrant.it;

import java.util.Objects;

public class Document {

    public String id;
    public String text;
    public String source;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id)
                && Objects.equals(text, document.text)
                && Objects.equals(source, document.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, source);
    }
}
