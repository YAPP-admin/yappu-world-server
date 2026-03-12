---
name: pre-commit-check
description: 커밋/푸시 전 코드 품질 검증 절차
---

git commit 또는 git push를 실행하기 전에 반드시 아래 절차를 수행할 것.

- [ ] ktlint 검사 실행: `./gradlew ktlintCheck`
  - 실패 시: `./gradlew ktlintFormat`으로 자동 수정 후 재실행
  - ktlintCheck가 통과할 때까지 커밋하지 말 것
- [ ] 테스트 실행: `./gradlew test`
  - 실패 시: 실패한 테스트를 수정한 후 재실행
  - 모든 테스트가 통과할 때까지 커밋하지 말 것
- [ ] 위 검증이 모두 통과한 경우에만 커밋/푸시 진행
