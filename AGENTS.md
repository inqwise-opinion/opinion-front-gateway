# Repository Guidelines

## Project Structure & Module Organization
This gateway is a Maven Vert.x service that relies on the shared `opinion-parent` BOM. Application sources live in `src/main/java`, grouped by responsibility (e.g., `com.inqwise.opinion.front.gateway.http`, `.handler`, `.security`) with the entry point `com.inqwise.opinion.front.gateway.MainVerticle` configured in `pom.xml`. Serve configuration files, JSON schemas, and templates from `src/main/resources`. Keep generated artifacts out of source control—Maven writes build output to `target`, and IntelliJ or VS Code metadata belongs in project-level ignores.

## Build, Test, and Development Commands
- `./mvnw clean compile` – resolves dependencies and validates the code compiles against Java 21.
- `./mvnw clean test` – runs the JUnit 5 + Vert.x test suite; use this before every commit.
- `./mvnw clean package` – produces the shaded fat JAR in `target/` for deployment.
- `./mvnw clean compile exec:java` – launches the gateway locally using Vert.x’s launcher; pass `-Dvertx.options...` flags to tweak runtime options. Sample curl: `curl -H 'Authorization: Bearer dev-token' http://localhost:8080/secure/hello`.

## Coding Style & Naming Conventions
Adopt standard Java conventions: 4-space indentation, braces on the same line, and `UpperCamelCase` classes with `lowerCamelCase` members. Constants stay in `UPPER_SNAKE_CASE`. Align new code with Vert.x idioms—prefer asynchronous handlers, avoid blocking calls on event-loop threads, and encapsulate each route or integration in its own verticle package for clarity. Use Log4j2 (`LogManager.getLogger(...)`) for structured logging; keep levels appropriate and prefer parameterized messages. Leverage your IDE’s “Reformat with Maven style” profile so imports stay ordered and unused imports are removed before committing.

## Testing Guidelines
Store tests in `src/test/java`, mirroring the package under test. Use `VertxExtension` for asynchronous flows and name test classes `*Test`. Unit tests that interact with the event bus should await completion with `VertxTestContext`. Target fast, deterministic tests; any integration-level scenarios should be annotated with `@Tag("integration")` and documented in the pull request. Log4j2 test configuration lives in `src/test/resources/log4j2-test.xml`; extend it to add appenders when debugging. Aim for meaningful coverage around request routing, error handling, and downstream service contracts rather than chasing a fixed percentage.

## Commit & Pull Request Guidelines
This repository has no published history yet, so adopt Conventional Commits (`feat: add survey proxy verticle`) to keep future automation simple. Keep summaries under 72 characters and describe motivation plus key changes in the body when needed. For pull requests, link to the relevant Jira ticket or GitHub issue, describe test evidence (command output or logs), and include screenshots or curl transcripts for user-facing changes. Ensure CI completes cleanly before requesting review.

## Security & Configuration Tips
Never commit secrets. Prefer environment variables or Vert.x configuration files excluded via `.gitignore`. Document any new external endpoints, auth requirements, or required config keys in the PR summary so operators can update deployment manifests promptly.
