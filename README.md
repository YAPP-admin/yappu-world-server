# 야뿌들을 위한 세계, yappu-world

## 🏛️ 아키텍처

### 시스템 아키텍처

![시스템 아키텍처](https://github.com/user-attachments/assets/4f185f42-3c4a-41f5-94b8-713dc15705c8)

### 패키지 아키텍처

- 특별히 아키텍처라고 할 건 없고, 다음의 사진과 같은 규칙을 따르고 있습니다.

![패키지 아키텍처](https://github.com/user-attachments/assets/b1d8c7b9-60ad-4bba-9d84-89635c323f98)


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
