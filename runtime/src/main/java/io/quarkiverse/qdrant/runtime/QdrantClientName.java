package io.quarkiverse.qdrant.runtime;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

/**
 * Qualifier used to specify the name of a Qdrant client.
 * <p>
 * Injection example:
 *
 * <pre>
 * &#64;Inject
 * &#64;QdrantClientName("secondary")
 * QdrantClient secondaryClient;
 * </pre>
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface QdrantClientName {

    String value();

    final class Literal extends AnnotationLiteral<QdrantClientName> implements QdrantClientName {

        private final String value;

        private Literal(String value) {
            this.value = value;
        }

        public static Literal of(String value) {
            return new Literal(value);
        }

        @Override
        public String value() {
            return value;
        }
    }
}
