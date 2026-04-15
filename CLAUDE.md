# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build
./gradlew build -x test   # skip tests

# Run
./gradlew bootRun

# Test
./gradlew test
./gradlew test --tests "ClassName"   # single test class
```

## Architecture

Spring Boot 3.3 / Java 21 social fitness platform. Packages are organized by domain under `src/main/java/Muscle/`:

| Package | Responsibility |
|---|---|
| `auth` | User accounts, JWT, OAuth2 (Naver/Kakao), follow/friend relationships |
| `post` | Posts with role-based visibility, likes, saves |
| `comment` | Post comments |
| `chat` | Real-time messaging via WebSocket/STOMP |
| `workout` | Individual workout entries (belong to a plan) |
| `workoutPlan` | Workout plans containing multiple workouts |
| `proRequest` | Requests to become a PRO trainer |
| `postReport` | Content moderation |
| `common` | Shared config, DTOs, exceptions, S3 service |

### Request Flow

1. `AuthInterceptor` validates `x-auth-token` JWT header (whitelist excludes public endpoints)
2. Controller delegates to Service
3. Service extracts `muscleId` (user ID) from the JWT via `JwtAuthToken.getClaims().getSubject()`
4. Service uses repositories and returns response wrapped in `ResponseDto`

### Key Conventions

**Authentication**: JWT via `x-auth-token` header. Passwords hashed with SHA-256 + salt (`SHA256Util`).

**Roles**: `AMATEUR`, `PRO`, `ADMIN`. PRO posts (`PostRole`) are visible only to PRO/ADMIN users.

**Entities**: Use `@GeneratedValue(strategy = AUTO)`, Lombok `@Data`/`@Builder`/`@NoArgsConstructor`. Prefer storing foreign key IDs (`writerId`, `senderId`) rather than JPA object relations.

**DTOs**: Inner static classes inside a parent DTO class (e.g., `RequestAuth.RegisterUserDto`). Request DTOs expose `toEntity()`, entity-adjacent code exposes `toDto()`.

**Responses**: All API responses are wrapped in `ResponseDto` (has `id`, `message`, `data` fields). HTTP 200 for success, 409 for conflicts, 500 for server errors.

**Services**: Use `Optional<String> token` parameter when auth is optional. Annotate mutating methods with `@Transactional`. Inject with `@RequiredArgsConstructor`.

### Infrastructure Dependencies

| Service | Default |
|---|---|
| MariaDB | `localhost:3306/muscle` |
| Redis | `localhost:6379` (email verification tokens, caching) |
| AWS S3 | credentials in `src/main/resources/secret/application-s3.properties` |
| SMTP | Spring Mail (email verification) |

Schema is managed by Hibernate `ddl-auto=update` — no migration tool in use.

### WebSocket

STOMP over WebSocket with SockJS fallback. Endpoint: `/ws`. Application prefix: `/app`. Broker destination: `/topic`. CORS allows EC2 host `43.201.91.43` and `localhost:*`.

### Configuration Files

- `src/main/resources/application.properties` — DB, Redis, Hibernate, Swagger
- `src/main/resources/application.yml` — OAuth2 (Naver, Kakao) client credentials
- `src/main/resources/secret/jwt-secret-key.properties` — JWT secret (loaded via `@PropertySource`)
- `src/main/resources/secret/application-s3.properties` — AWS credentials
- `src/main/resources/secret/secret.properties` — other secrets

### API Docs

Swagger UI available at `http://localhost:8080/swagger-ui/` when running locally. Security scheme: API key in `x-auth-token` header.
