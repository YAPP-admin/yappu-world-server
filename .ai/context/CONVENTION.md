# 컨벤션

## 주요 명령어

```bash
./gradlew clean build             # 전체 빌드 (테스트 포함)
./gradlew test                    # 전체 테스트
./gradlew ktlintCheck             # ktlint 검사
./gradlew ktlintFormat            # ktlint 자동 포맷
```

코드 변경 후 반드시 `./gradlew ktlintCheck` 실행. 위반 시 `./gradlew ktlintFormat`으로 자동 수정.

## 코드 스타일

### ktlint 규칙 (.editorconfig)

- indent: space 4칸
- max line length: 120자
- trailing comma: 사용하지 않음
- 비활성화된 규칙: `comment-spacing`, `import-ordering`, `string-template-indent`, `multiline-expression-wrapping`, `no-empty-first-line-in-class-body`, `no-blank-line-before-rbrace`, `backing-property-naming`
- 테스트 코드: `annotation` 규칙 추가 비활성화

### 코딩 규칙

- Swagger 설명 (`@Tag`, `@Operation`, `@Schema` 등)과 에러 메시지는 **한국어**
- 클래스/변수/메서드명은 **영어**
- `!!` (non-null assertion) 사용 금지 → `?.let`, `?:`, `?.also` 등 안전한 nullable 처리
- `@Autowired` 필드 주입 금지 → 생성자 주입만 사용
- `as Any`, `@Suppress`, `@SuppressWarnings` 등 타입 안전성 우회 금지
- 새 의존성 추가 시 `gradle/libs.versions.toml` Version Catalog 사용

## 테스트

### 프레임워크

- **Kotest FeatureSpec** (`feature { }` / `scenario { }`) + **MockK** 만 사용
- JUnit assertion, Mockito 사용 금지

### 테스트 패턴

```kotlin
class SomeServiceTest @Autowired constructor(
    private val someRepository: SomeRepository
) : CustomDataJpaTestFeatureSpec({

    lateinit var service: SomeService

    beforeEach {
        service = SomeService(/* 수동 의존성 주입 */)
    }

    feature("기능 설명") {
        scenario("시나리오 설명") {
            // given - when - then
            result shouldBe expected
            result.shouldNotBeNull()
            list shouldHaveSize 3
        }
    }
})
```

### 테스트 인프라

- 통합 테스트 기반 클래스: `CustomDataJpaTestFeatureSpec` (JPA), `SpringBootTestFeatureSpec`
- Fixture: `src/test/kotlin/co/yappuworld/support/fixture/` 디렉토리
- `beforeEach`에서 서비스를 수동 생성하여 의존성 주입하는 패턴을 따른다

## Git 컨벤션

### 브랜치

- `dev` (개발), `prod` (운영)
- 네이밍: `feat/{기능명}`, `fix/{이슈명}`, `infra/{작업명}`, `chore/{작업명}`

### 커밋 메시지

한국어. 접두사 필수.

- `feat:` 새 기능
- `fix:` 버그 수정
- `infra:` 인프라/배포
- `chore:` 기타 작업
- 예: `feat: 가입신청서 데이터 이관 및 목록 조회 고도화 (#203)`

### PR

- `dev` 브랜치 대상
- CI에서 `./gradlew clean build` (test profile) 자동 실행
- RCA 리뷰 룰: R(Request changes), C(Comment), A(Approve)

## 보안

- `application-secret-local.yaml`, `.secrets` 절대 커밋 금지
- Docker Compose 파일: `docker/` 디렉토리
