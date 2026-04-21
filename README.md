# Quarkus Qdrant
[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.qdrant/quarkus-qdrant?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.qdrant/quarkus-qdrant-parent)

A Quarkus extension for [Qdrant](https://qdrant.tech/), the open-source vector database. It wraps the Qdrant REST API as a lightweight alternative to the official Java gRPC-based client.

## Get Started

Add the dependency to your project:

```xml
<dependency>
    <groupId>io.quarkiverse.qdrant</groupId>
    <artifactId>quarkus-qdrant</artifactId>
    <version>${quarkus-qdrant.version}</version>
</dependency>
```

Inject the client and start using it:

```java
@Inject
QdrantClient qdrant;

qdrant.createCollection("docs").vectorSize(384).distance("Cosine").execute();

qdrant.upsert("docs").point(new PointStruct(id, vector, payload)).execute();

List<ScoredPoint> results = qdrant.search("docs").vector(queryVec).limit(10).execute();
```

In dev mode, a Qdrant container starts automatically via Dev Services.

## Documentation

The documentation for this extension is available at https://docs.quarkiverse.io/quarkus-qdrant/dev/index.html.

