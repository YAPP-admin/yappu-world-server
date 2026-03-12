# 데이터베이스

## DB 환경

| 환경 | DB | 비고 |
|---|---|---|
| 운영 (prod) | Oracle | OCI 배포 |
| 개발 (dev) | Oracle | OCI 배포 |
| 로컬 (local) | MySQL | Spring Docker Compose Support로 자동 실행 |
| 테스트 (test/CI) | H2 | 인메모리, CI 환경 |

## ORM

- **JPA** + **Kotlin JDSL 3.5.5** (타입 안전한 쿼리 DSL)
- SQL 로깅: p6spy (개발 환경)

## Kotlin JDSL

### 설정

- `JdslConfig`: `JpqlRenderContext` Bean 등록
- `CustomJdslJpqlExecutor`: `singleOrNull` 쿼리 실행을 위한 커스텀 인터페이스 (EntityManager + JpqlRenderContext 기반)

### Repository 통합

JPA Repository는 `KotlinJdslJpqlExecutor`를 함께 상속하여 JDSL 쿼리를 지원한다.

```kotlin
interface UserRepository :
    JpaRepository<UserEntity, UUID>,
    KotlinJdslJpqlExecutor
```

### Custom DSL 클래스

복잡한 쿼리 로직은 `Custom{Domain}Dsl` 클래스로 분리한다. `Jpql()`을 상속하고 companion object로 `Constructor`를 구현하는 패턴을 따른다.

```kotlin
@Component
class CustomUserDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomUserDsl> {
        override fun newInstance(): CustomUserDsl = CustomUserDsl()
    }

    fun selectFromUserWithActivityUnit(): SelectQueryWhereStep<UserWithActivityUnit> =
        selectNew<UserWithActivityUnit>(
            path(UserEntity::getId),
            path(UserEntity::email),
            // ...
        ).from(
            entity(ActivityUnitEntity::class),
            innerJoin(entity(UserEntity::class))
                .on(path(ActivityUnitEntity::userId).equal(path(UserEntity::getId)))
        )
}
```

**현재 프로젝트의 Custom DSL 클래스**: `CustomUserDsl`, `CustomSessionDsl`, `CustomAttendanceDsl`, `CustomTeamDsl`

### 쿼리 실행 패턴

**1. Repository의 `findAll` + Custom DSL**
```kotlin
userRepository.findAll(CustomUserDsl) { getActiveUser(generation) }.filterNotNull()
```

**2. Repository의 `findPage` + 인라인 DSL**
```kotlin
userRepository.findPage(pageRequest) {
    selectNew<Dto>(...)
        .from(entity(...))
        .whereAnd(*predicates.toTypedArray())
        .orderBy(...)
}.filterNotNull()
```

**3. `jpql { }` + EntityManager 직접 실행** (복잡한 단건 조회 시)
```kotlin
jpql {
    selectNew<Dto>(...).from(...).where(...)
}.let { query ->
    entityManager.createQuery(query, context).singleResult
}
```

### 주요 DSL API

- `selectNew<DTO>(...)`: DTO 프로젝션
- `path(Entity::field)`: 필드 참조
- `entity(Entity::class)`: FROM 대상
- `innerJoin(...).on(...)`: 조인
- `.whereAnd(...)` / `.where(...)`: 조건
- `.orderBy(...)`: 정렬
- `customExpression(...)`: 네이티브 SQL 표현식 (예: `ROW_NUMBER() OVER (...)`)

## Entity 규칙

모든 Entity는 `BaseEntity`를 상속한다.

```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : Persistable<UUID> {
    @Id
    @Column
    private val id: UUID = UlidCreator.getMonotonicUlid().toUuid()

    @CreatedDate
    open var createdAt: LocalDateTime = DatetimeUtils.getCurrentDateTimeInKST()

    @LastModifiedDate
    open var updatedAt: LocalDateTime = DatetimeUtils.getCurrentDateTimeInKST()
}
```

- **PK**: ULID 기반 UUID — `UlidCreator.getMonotonicUlid().toUuid()`로 자동 생성. ID를 직접 생성하지 말 것
- **Auditing**: `createdAt`, `updatedAt` 자동 관리 (KST 기준)
- **새 Entity 생성 시**: `BaseEntity()` 상속 후 필드만 추가

## Repository 구조

JPA Repository를 직접 노출하지 않고, FindService/CommandService로 래핑한다.

```
infrastructure/
├── jpa/
│   └── {Domain}Repository.kt       ← interface, Spring Data JPA
├── {Domain}FindService.kt           ← @Service, 조회 전용
└── {Domain}CommandService.kt        ← @Service, 쓰기 전용
```

- FindService: `findById`, `findAll`, 조건 검색 등 조회 메서드
- CommandService: `save`, `delete`, 상태 변경 등 쓰기 메서드
- Controller/Application Service에서는 FindService/CommandService만 의존한다

## Flyway 마이그레이션

- 위치: `src/main/resources/db/migration/`
- 네이밍: `V{번호}__{설명}.sql` (예: `V1__add_applicant_name_and_activity_unit_table.sql`)
- **기존 마이그레이션 파일을 절대 수정하지 말 것** — 새 마이그레이션 파일을 추가할 것
- 번호는 기존 마이그레이션의 다음 번호를 사용

## 컨버터

- `LocalDateStringConverter`: LocalDate ↔ String 변환
- `LocalTimeStringConverter`: LocalTime ↔ String 변환
- `ApplicationDetailsConverter`: 가입신청서 상세 정보 JSON 변환
