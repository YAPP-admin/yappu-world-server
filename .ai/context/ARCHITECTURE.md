# 아키텍처

## 기술 스택

- **언어**: Kotlin 2.1.10
- **프레임워크**: Spring Boot 3.4.1 (Web MVC, Security, JPA, Validation)
- **JDK**: 21 (Liberica)
- **빌드**: Gradle (Version Catalog - `gradle/libs.versions.toml`)
- **DB**: Oracle (운영), MySQL, H2 (테스트) + Flyway 마이그레이션
- **ORM**: JPA + Kotlin JDSL 3.5.5
- **인증**: JWT (jjwt 0.12.5)
- **API 문서**: SpringDoc OpenAPI 2.7.0
- **테스트**: JUnit 5 + Kotest 5.9.1 (FeatureSpec) + MockK 1.13.14
- **코드 품질**: ktlint 1.5.0 (커밋 전 검증 절차 운영)
- **모니터링**: Sentry, Prometheus, Loki
- **외부 연동**: Firebase FCM, Kakao Map API, Discord Webhook

## 프로젝트 구조

### 멀티모듈 (전환 중)

현재 멀티모듈 전환이 진행 중이다. 대부분의 비즈니스 로직은 아직 `src/`에 존재한다.

```text
yappu-world-server/
├── src/                         ← ★ 현재 모든 비즈니스 로직이 여기 있음
├── bootstrap/                   ← Spring Boot Application 진입점
├── api/                         ← Controller, Swagger (depends: application, common)
├── application/                 ← 유즈케이스 (아직 이관 전)
├── domain/                      ← 도메인 모델 (아직 이관 전)
├── infrastructure/              ← JPA, DB, FCM, Flyway (depends: domain)
└── common/                      ← JWT, Spring Context
```

새 코드를 작성할 때는 기존 `src/` 구조의 패턴을 따를 것. 멀티모듈 이관 작업이 아닌 이상 새 모듈 디렉토리에 코드를 추가하지 말 것.

### 패키지 구조

모든 도메인은 `src/main/kotlin/co/yappuworld/` 아래에서 동일한 계층 구조를 따른다:

```text
{domain}/
├── client/
│   ├── presentation/           ← Controller + Swagger Api interface
│   │   ├── {Domain}Api.kt          ← interface. Swagger 어노테이션 정의
│   │   └── {Domain}Controller.kt   ← @RestController. Api interface 구현
│   ├── application/            ← Service (유즈케이스)
│   │   └── {Domain}Service.kt      ← @Service. 비즈니스 로직
│   └── dto/
│       ├── request/            ← 요청 DTO
│       └── response/           ← 응답 DTO
├── domain/                     ← 도메인 모델, VO, 비즈니스 규칙
│   ├── model/                  ← 도메인 모델
│   └── vo/                     ← Value Object, enum (Error 포함)
└── infrastructure/             ← 외부 의존성 구현
    ├── entity/                 ← JPA Entity (@Entity)
    ├── jpa/                    ← Spring Data JPA Repository (interface)
    ├── model/                  ← JDSL 프로젝션 DTO (일부 도메인에 존재)
    ├── dto/                    ← 인프라 계층 전용 DTO (일부 도메인에 존재)
    ├── Custom{Domain}Dsl.kt    ← Kotlin JDSL 커스텀 DSL (@Component)
    ├── {Domain}FindService.kt  ← 조회 전용 서비스 (@Service)
    └── {Domain}CommandService.kt ← 쓰기 전용 서비스 (@Service)
```

**도메인 목록**: `user`, `schedule`, `team`, `post`, `operation`
**공통**: `global` (config, exception, filter, persistence, response, security, util)
**외부 연동**: `external` (fcm, map, messenger)

## 계층 간 의존성

- `presentation` → `application` → `infrastructure` 방향으로만 의존
- `domain` 패키지는 `client`, `infrastructure` 같은 바깥 계층에 의존하지 않는다
- 다른 도메인의 `domain` 패키지 참조는 가능하지만, 필요한 범위로 최소화한다
- Controller는 반드시 Api interface를 구현하고, 비즈니스 로직은 Service에 위임한다
- infrastructure의 FindService/CommandService가 JPA Repository를 감싸서 제공한다. Controller나 Service에서 JPA Repository를 직접 사용하지 않는다

## Controller 패턴

- Api interface (`{Domain}Api.kt`)에 `@Tag`, `@Operation`, `@ApiResponses` 등 Swagger 어노테이션을 정의
- Controller (`{Domain}Controller.kt`)는 `@RestController`로 Api interface를 구현
- 응답은 `ResponseEntity<SuccessResponse<T>>` 래퍼를 사용
- URL 패턴: `/admin/v1/{resource}` (어드민), `/v1/{resource}` (일반)

## Service 패턴

- `@Service` + 생성자 주입 (constructor injection)
- 읽기 전용 메서드: `@Transactional(readOnly = true)`
- 쓰기 메서드: `@Transactional`
- 예외 발생: `throw BusinessException(XxxError.ERROR_NAME)`

## 에러 패턴

도메인별 `enum class {Domain}Error : Error`를 정의한다.

```kotlin
// domain/vo/{Domain}Error.kt
enum class TeamError : Error {
    TEAM_NOT_FOUND {
        override val message: String = "팀을 찾을 수 없습니다."
        override val code: String = "TEAM_0001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    }
}

// 사용: throw BusinessException(TeamError.TEAM_NOT_FOUND)
```

- 각 에러는 `message` (한국어), `code` (도메인 접두사 + 번호), `type` (ErrorType) 프로퍼티를 가진다
- 에러 코드 규칙: `{DOMAIN}_{4자리 번호}` (예: `TEAM_0001`, `USER_1001`)
  - `0xxx`: 조회 실패 (NOT_FOUND 계열)
  - `1xxx`: 비즈니스 규칙 위반 (BAD_REQUEST, WRONG_STATE 계열)
- `ErrorType`: `WRONG_ARGUMENT`, `BAD_REQUEST`, `UNAUTHORIZED`, `FORBIDDEN`, `NOT_FOUND`, `WRONG_STATE`, `UNEXPECTED_ERROR`

## 응답 패턴

- 성공: `SuccessResponse<T>(data)` — `{ "data": ..., "isSuccess": true }`
- 실패: `ErrorResponse(message, errorCode)` — `{ "message": ..., "errorCode": ..., "isSuccess": false }`
- 페이징: `OffsetPageResponse<T>`, `CursorPageResponse<T>`
