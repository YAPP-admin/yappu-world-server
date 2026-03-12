# yappu-world-server

YAPP 동아리 회원 관리 백엔드 서버. Kotlin 2.1.10 + Spring Boot 3.4.1 + JDK 21.

## 컨텍스트 문서

코드를 작성하거나 수정하기 전에 아래 문서를 반드시 참고할 것.

- `.ai/context/ARCHITECTURE.md` — 기술 스택, 프로젝트 구조, 패키지 계층, Controller/Service/Entity/에러/응답 패턴
- `.ai/context/DATABASE.md` — DB 환경, Entity 규칙 (BaseEntity, ULID), Repository 구조, Flyway 마이그레이션
- `.ai/context/CONVENTION.md` — 빌드 명령어, 코드 스타일 (ktlint), 테스트 패턴 (Kotest/MockK), Git 컨벤션

## 스킬

- `.ai/skill/pre-commit-check.md` — 커밋/푸시 전 코드 품질 검증 절차
- `.ai/skill/dependency-check.md` — 패키지 의존 관계 규칙 위반 검증 절차

`git commit` 또는 `git push`를 실행하기 전에 반드시 `.ai/skill/pre-commit-check.md`의 절차를 따를 것. 검증이 실패하면 커밋하지 말 것.

코드를 작성하거나 수정한 후에는 `.ai/skill/dependency-check.md`의 의존 관계 규칙을 위반하지 않았는지 검증할 것.
