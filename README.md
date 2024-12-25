# 야뿌들을 위한 세계, yappu-world

## ⚙️ 프로젝트 세팅

### ktlint 설정
```
mkdir .git/hooks
./gradlew addKtlintCheckGitPreCommitHook
```

### 프로젝트 실행
- spring docker compose support를 이용하여 세팅
- 별도의 처리 없이 `YappuWorldServerApplication.kt` 실행

## 참고 사항

### ktlint 기준 변경시
```
// .editorconfig 수정 후
./gradlew addKtlintCheckGitPreCommitHook
```
