# Muscle

피트니스 소셜 플랫폼. 운동 기록 관리, 트레이너 매칭, 커뮤니티 기능을 제공합니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Backend | Spring Boot 3.3, Java 21 |
| Database | MariaDB, Spring Data JPA, QueryDSL |
| Cache | Redis |
| Storage | AWS S3 |
| Auth | JWT, OAuth2 (Naver, Kakao) |
| Real-time | WebSocket (STOMP + SockJS) |
| Docs | Swagger / OpenAPI 3 |

## 주요 기능

- **회원 관리** — 이메일 인증 회원가입, 일반 로그인, 네이버/카카오 소셜 로그인, 회원 레벨 시스템 (AMATEUR / PRO / ADMIN)
- **소셜** — 팔로우/팔로잉, 친구 요청 시스템, 레벨 기반 유저 매칭
- **커뮤니티** — 이미지 첨부 게시글, 좋아요/저장, 댓글, 게시판별/역할별 피드, 신고 및 관리자 모더레이션
- **운동 기록** — 월별 운동 계획 생성·관리, 운동 완료 체크, 친구 운동 계획 공유
- **실시간 채팅** — WebSocket 기반 1:1 채팅, 채팅 내역 페이지네이션
- **PRO 트레이너** — PRO 신청 및 자격증 이미지 업로드, 관리자 심사, PRO 전용 게시글
- **헬스장 검색** — 네이버 지도 API 연동 위치 기반 헬스장 탐색

## 아키텍처

```
Client
  │
  ├── REST API (x-auth-token JWT 헤더)
  │     AuthInterceptor → Controller → Service → Repository → MariaDB
  │
  └── WebSocket (STOMP /ws)
        ChatController → ChatService → Redis / MariaDB

AWS S3 ← S3Service (프로필 이미지, 게시글 이미지, 자격증 이미지)
Redis   ← 이메일 인증 토큰, 채팅 메시지 임시 저장
```

**패키지 구조** (`src/main/java/Muscle/`)

```
auth/         # 회원, JWT, OAuth2, 팔로우, 친구
post/         # 게시글, 좋아요, 저장, 신고
comment/      # 댓글
chat/         # 실시간 채팅 (WebSocket)
workout/      # 운동 항목
workoutPlan/  # 운동 계획
proRequest/   # PRO 트레이너 신청
postReport/   # 게시글 신고
gymSearch/    # 헬스장 검색
common/       # 공통 설정, 예외처리, S3 서비스
```

## API 엔드포인트 요약

| 도메인 | Base URL | 주요 기능 |
|---|---|---|
| 인증/회원 | `/api/auth` | 회원가입, 로그인, OAuth2, 프로필 수정, 유저 검색 |
| 팔로우 | `/api/follow` | 팔로우/언팔로우, 팔로워/팔로잉 조회 |
| 친구 | `/api/friend` | 친구 요청, 수락/거절, 친구 목록 |
| 게시글 | `/api/post` | CRUD, 좋아요, 저장, 피드, 검색 |
| 댓글 | `/api/comment` | CRUD |
| 운동 | `/api/workout` | CRUD, 완료 체크 |
| 운동계획 | `/api/workoutPlan` | 월별 계획 조회, 친구 계획 공유 |
| PRO 신청 | `/api/proRequest` | 신청, 심사, 승인/거절 |
| 신고 | `/api/postReport` | 신고 등록/조회 |
| 채팅 | `/api/chat` + WS `/ws` | 채팅방, 메시지 조회, 실시간 메시지 |
| 헬스장 | `/api/gym` | 위치 기반 검색 |

전체 API 명세: `http://localhost:8080/swagger-ui/`

## 실행 방법

### 사전 요구사항

- Java 21
- MariaDB
- Redis

### 환경 설정

`src/main/resources/secret/` 디렉토리를 생성하고 아래 파일들을 작성합니다. (각 `.example` 파일 참고)

```
secret/jwt-secret-key.properties
secret/application-s3.properties
secret/secret.properties
```

`application.properties`에서 DB 접속 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/muscle
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

`application.yml`에서 OAuth2 클라이언트 정보를 설정합니다.

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: YOUR_NAVER_CLIENT_ID
            client-secret: YOUR_NAVER_CLIENT_SECRET
          kakao:
            client-id: YOUR_KAKAO_CLIENT_ID
```

### 실행

```bash
# DB 스키마는 Hibernate가 자동 생성 (ddl-auto=update)
./gradlew bootRun
```

## 인증 방식

모든 인증 필요 API는 요청 헤더에 JWT 토큰을 포함해야 합니다.

```
x-auth-token: <JWT_TOKEN>
```

토큰은 로그인 API 응답의 `data` 필드에서 발급됩니다.
