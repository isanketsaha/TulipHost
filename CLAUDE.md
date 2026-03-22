# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TulipHost is a Spring Boot-based school management system built with JHipster. It handles student enrollment, fee management, employee management, inventory, and reporting for Tulip School.

## Build & Development Commands

```bash
# Run the application (dev profile by default)
./mvnw

# Run with specific profile
./mvnw -Pdev
./mvnw -Pprod

# Run tests (excludes integration tests)
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName

# Run integration tests (requires Docker for Testcontainers)
./mvnw verify

# Start MySQL database (required for local development)
docker compose -f src/main/docker/mysql.yml up -d

# Build without tests
./mvnw package -DskipTests

# Code formatting (Spotless)
./mvnw spotless:apply

# Build Docker image with Jib
./mvnw jib:dockerBuild
```

## Architecture

### Package Structure

- `domain/` - JPA entities (AbstractAuditingEntity base class for auditing)
- `repository/` - Spring Data JPA repositories extending BaseRepository; use QueryDSL for complex queries
- `service/` - Business logic layer
- `mapper/` - MapStruct mappers for DTO transformations
- `web/rest/` - REST controllers (context path: `/api`)
- `data/` - DTOs (Data Transfer Objects)
- `enums/` - Application enumerations
- `config/` - Spring configuration classes
- `client/` - Feign clients for external services (eOffice, msg-gateway)
- `state/` - Spring State Machine configuration

### Key Patterns

**Use Lombok everywhere** - All classes should use Lombok annotations (@Data, @Builder, @Getter, etc.) to avoid boilerplate.

**Use MapStruct for transformations** - Create mapper interfaces in `mapper/` package for entity-to-DTO conversions:

```java
@Mapper(componentModel = "spring")
public interface StudentMapper {
  StudentDTO toDto(Student entity);
}

```

**Use QueryDSL for database queries** - Complex queries should use QueryDSL with generated Q-classes in `target/generated-sources/java`.

### Database

- MySQL with Liquibase for migrations (disabled by default via `no-liquibase` profile)
- Flyway also configured for schema versioning
- HikariCP connection pool
- Hibernate with CamelCaseToUnderscores naming strategy

### Testing

- Unit tests: JUnit 5 with `@SpringBootTest`
- Integration tests: Use `@IntegrationTest` annotation (includes Testcontainers for MySQL)
- Tests run alphabetically for reproducibility
- Testcontainers provides embedded MySQL for integration tests

### External Integrations

- **eOffice API** - Attendance tracking via Feign client
- **MSG91** - SMS gateway for notifications
- **AWS S3** - Object storage for documents
- **JasperReports** - PDF report generation

### Profiles

- `dev` - Development (default, includes devtools)
- `prod` - Production
- `no-liquibase` - Disabled by default
- `api-docs` - Enable OpenAPI documentation

## Security & Configuration — Externalizing Sensitive Values

**Rule: No secrets in committed yml files.** All passwords, tokens, and keys must be injected via environment variables.

### How it works

`.vscode/launch.json` is already wired to load `.env` from the project root:

```json
"envFile": "${workspaceFolder}/.env"
```

Spring Boot yml files reference env vars using `${ENV_VAR_NAME}` placeholder syntax. The `.env` file (never committed) holds the actual values.

### `.env` file (gitignored — never commit this)

Create `.env` at the project root with the following keys:

```dotenv
# Database
DB_URL=jdbc:mysql://localhost:2306/tulip?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
DB_USERNAME=root
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_base64_encoded_jwt_secret

# Mail (Zoho SMTP)
MAIL_HOST=smtp.zoho.in
MAIL_PORT=587
MAIL_USERNAME=bot@tulipschool.co.in
MAIL_PASSWORD=your_smtp_password

# Feign API tokens
EOFFICE_TOKEN=Basic <base64_encoded_credentials>
MSG_GATEWAY_TOKEN=your_msg91_auth_key

# AWS S3
S3_REGION=ap-northeast-1
S3_KEY=your_aws_access_key
S3_SECRET=your_aws_secret_key
S3_BUCKET=your_bucket_name
S3_INVOICE_BUCKET=your_invoice_bucket_name

# Twilio / WhatsApp / TinyURL
TWILIO_ACCOUNT_SID=your_twilio_asid
TWILIO_KEY=your_twilio_key
TWILIO_MESSAGE_SID=your_twilio_msid
TINYURL_KEY=your_tinyurl_key
WHATSAPP_INTEGRATED_NUMBER=your_whatsapp_number
WHATSAPP_NAMESPACE=your_whatsapp_namespace
DEFAULT_EMAIL=default@example.com
DEFAULT_PHONE=+910000000000
```

### Corresponding yml placeholders

| yml key                                              | env var                     |
| ---------------------------------------------------- | --------------------------- |
| `spring.datasource.url`                              | `${DB_URL}`                 |
| `spring.datasource.username`                         | `${DB_USERNAME:root}`       |
| `spring.datasource.password`                         | `${DB_PASSWORD}`            |
| `spring.mail.host`                                   | `${MAIL_HOST:smtp.zoho.in}` |
| `spring.mail.port`                                   | `${MAIL_PORT:587}`          |
| `spring.mail.username`                               | `${MAIL_USERNAME}`          |
| `spring.mail.password`                               | `${MAIL_PASSWORD}`          |
| `jhipster.security.authentication.jwt.base64-secret` | `${JWT_SECRET}`             |
| `app.feign.auth.tokens.eoffice`                      | `${EOFFICE_TOKEN}`          |
| `app.feign.auth.tokens.msg-gateway`                  | `${MSG_GATEWAY_TOKEN}`      |
| `application.aws.credential.accessKey`               | `${S3_KEY}`                 |
| `application.aws.credential.secret`                  | `${S3_SECRET}`              |

### .gitignore

Ensure `.env` is listed in `.gitignore`. Never use `.env.example` with real values — use placeholder strings only.

### Production

In prod (CI/CD or server), inject the same env vars via the deployment environment (e.g., GitHub Actions secrets, Docker `--env-file`, or systemd `EnvironmentFile`). Do not use `.env` files in production.

---

## Code Guidelines

1. Keep code small and reusable
2. Use Lombok to avoid boilerplate
3. Use MapStruct for object transformations
4. Use QueryDSL for database queries
5. Place utilities in `utils/` and constants in dedicated classes
6. Implement happy path first, iterate incrementally - don't over-engineer
7. Always follow SOLID principal.
8. comment code those are sitting idle and have no importance.
9. Always decide on the tables and fields and then create table post that services and later the api. You should ask question if you need.
10. what all api we need and tables we need to create to bring capability will be understood by reviewing the ui .
11. suggest to create UI if that applicable.
12. Always start small and iterate — introduce the simplest solution that works first (e.g., S3 direct URLs before CloudFront, in-memory cache before Redis). Add complexity only when there is a clear, demonstrated need.
