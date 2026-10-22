# Contributing Guide

**Want to contribute? Great!** All contributions are welcome: bug reports, fixes, documentation, examples.

This extension is part of the Quarkus ecosystem. Contributions should follow the same principles when applicable: [quarkusio/quarkus · CONTRIBUTING](https://github.com/quarkusio/quarkus/blob/main/CONTRIBUTING.md)

## Reporting an Issue

This project uses GitHub issues. Open an issue directly in [quarkiverse/quarkus-qdrant](https://github.com/quarkiverse/quarkus-qdrant/issues).

If you found a bug, please include:
- Steps to reproduce
- What you see vs. what you expect
- Versions: Quarkus, Java, Maven, GraalVM

## Tests and Documentation Are Not Optional

Include tests in your pull requests. For new features, add or update the reference documentation under `docs/`.

## Backward Compatibility

We support the **three most recent Qdrant minor releases**, and nothing older. Today that is 1.18, 1.19 and 1.20. The window moves with every Qdrant release: a new minor joins it, the oldest one leaves it. The `compatibility` job in [build.yml](.github/workflows/build.yml) runs the test suite against each version in the window, so the claim stays true instead of being assumed.

Keeping the window to three means we do not carry workarounds for servers nobody in the window runs. It also means the matrix has to be moved forward on every Qdrant minor release, not only when something breaks.

Within the window, compatibility still has to be protected by hand. Qdrant's REST API does not reject unknown fields. A field an older server does not understand is dropped silently and the call still returns 200. A change that narrows compatibility therefore fails quietly, giving wrong behaviour on older servers rather than an error. Three rules keep that from happening.

**Add fields. Never rename, retype or remove one.** Renaming `SearchRequest.vector` to `query` once moved the floor from any 1.x up to 1.10, and nobody noticed until later.

**Use boxed types for new fields.** `Integer`, `Boolean`, `Float`, not `int`, `boolean`, `float`. When an older server omits a field, a primitive reads as `0` or `false`, which looks like real data. A boxed type reads as `null`.

**Keep `@JsonInclude(NON_NULL)` on request DTOs.** It keeps an unset field off the wire entirely, so an older server never sees it. It does not omit primitives, which is the second reason to avoid them.

To try a change against the oldest server in the window, override the image. It is resolved in two places, Dev Services reads it from build-time config and the TLS tests read a Maven property, so pass both:

```shell
QDRANT_IMAGE=docker.io/qdrant/qdrant:v1.18-unprivileged

mvn verify \
  -Dqdrant.container.image=$QDRANT_IMAGE \
  -Dquarkus.qdrant.devservices.qdrant-image-name=$QDRANT_IMAGE
```

When a new Qdrant release lands, the [watch workflow](.github/workflows/qdrant-api-watch.yml) opens an issue carrying this checklist, including moving the matrix forward.

## Prerequisites

- Java 17+
- Apache Maven
- Docker (required for integration tests and Dev Services)

## Building

```shell
# Build everything
mvn verify

# Build a specific module
cd runtime && mvn verify
cd deployment && mvn verify
```

## Running Integration Tests

Integration tests require a running Qdrant instance. Dev Services handles this automatically via Docker:

```shell
# JVM mode
mvn verify

# Native mode
mvn verify -Dnative
```


## For Maintainers: Releasing

Releases are fully automated via GitHub Actions. The process is:

### 1. Open a PR updating `.github/project.yml`

Set `current-version` to the version you want to release and keep `next-version` as `999-SNAPSHOT`:

```yaml
release:
  current-version: "1.0.0"
  next-version: "999-SNAPSHOT"
```

Opening this PR automatically triggers the [pre-release workflow](.github/workflows/pre-release.yml), which validates the release.

### 2. Merge the PR

Merging triggers the [prepare-release workflow](.github/workflows/release-prepare.yml), which runs the Maven release and pushes the release tag to GitHub.

The tag push then triggers the [perform-release workflow](.github/workflows/release-perform.yml), which deploys the artifact to Maven Central via Sonatype.

> **Important:** The PR must come from the origin repository, not a fork. Release secrets are not propagated to forks.

The [perform-release workflow](.github/workflows/release-perform.yml) also creates the GitHub Release automatically and kicks off the next development iteration workflow, which bumps the version back to `999-SNAPSHOT`. No manual step needed.
