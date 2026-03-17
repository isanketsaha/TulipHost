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
