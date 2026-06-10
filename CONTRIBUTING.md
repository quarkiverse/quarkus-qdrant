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
