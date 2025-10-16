# Repository Guidelines

## Project Structure & Module Organization
- Java source: `src/main/java/com/finance/{config,controller,domain,form,repository,service}`.
- App entry: `src/main/java/com/finance/PersonalFinanceManagerApplication.java`.
- Views: `src/main/resources/templates` (Thymeleaf); static assets: `src/main/resources/static/{css,js,images}`.
- Config: `src/main/resources/application.properties`.
- Tests: `src/test/java/com/finance/...`.

## Build, Test, and Development Commands
- Build (compile only): `mvn clean compile` — resolves deps and compiles Java 8 sources.
- Run locally (dev): `mvn spring-boot:run` — starts on `http://localhost:8080`.
- Package JAR: `mvn clean package` — outputs `target/personal-finance-manager-1.0.0.jar`.
- Run JAR: `java -jar target/personal-finance-manager-1.0.0.jar`.
- Tests: `mvn test` — runs unit/integration tests.
- Use H2 in-memory DB for dev by enabling H2 lines in `application.properties` (example):
  - `spring.datasource.url=jdbc:h2:mem:testdb`
  - `spring.h2.console.enabled=true`

## Coding Style & Naming Conventions
- Language: Java 8; indent with 4 spaces; UTF‑8 encoding.
- Packages: `com.finance.*` (lowercase). Classes: `UpperCamelCase`.
- Suffixes: controllers `*Controller`, services `*Service`, repositories `*Repository`, entities in `domain`.
- Templates: kebab-case `.html` in `templates/`; static files under `static/`.
- Prefer constructor injection; avoid field injection in Spring components.

## Testing Guidelines
- Frameworks: Spring Boot Test, JUnit (via `spring-boot-starter-test`).
- Location: mirror package under `src/test/java`.
- Naming: `ClassNameTest` for unit tests; annotate integration tests with `@SpringBootTest` when needed.
- Run: `mvn test`. No coverage threshold enforced yet; keep tests fast and isolated.

## Commit & Pull Request Guidelines
- If no existing convention is evident, use Conventional Commits (e.g., `feat: add budget summary API`).
- PRs should include: clear description, linked issue (if any), steps to verify, screenshots for UI changes, and notes on DB/config changes.
- Keep changes scoped; update `README.md` or `application.properties` examples when behavior/config changes.

## Security & Configuration Tips
- Do not commit secrets. Prefer env vars for DB creds (e.g., `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`).
- Validate user input; rely on Bean Validation and service-layer checks.
- Review `SecurityConfig.java` when adding routes to ensure correct authorization.

