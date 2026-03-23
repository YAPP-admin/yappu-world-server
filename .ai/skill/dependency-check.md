---
name: dependency-check
description: 코드 변경 후 패키지 의존 관계 규칙 위반 여부 검증
---

코드를 작성하거나 수정한 후, 아래 의존 관계 규칙을 위반하지 않았는지 검증할 것.

## 의존 방향

```text
presentation → application → infrastructure
                                ↑
                             domain (어디서든 참조 가능하나, domain이 다른 계층을 참조하면 안 됨)
```

## 검증 체크리스트

- [ ] **Service → JpaRepository 직접 참조 금지**: `client/application/` 내 Service가 `infrastructure/jpa/` 패키지의 Repository 인터페이스를 직접 import하면 위반. FindService/CommandService를 통해 접근할 것
- [ ] **Controller → Infrastructure 직접 참조 금지**: `client/presentation/` 내 Controller가 `infrastructure/` 패키지를 직접 import하면 위반. 반드시 `client/application/` Service를 통해 접근할 것
- [ ] **cross-domain domain 의존 최소화**: `{domainA}/domain/`에서 `{domainB}/domain/`을 import하는 경우, 해당 의존이 정말 필요한지 검토할 것. VO/enum 참조는 허용하되, 모델 간 직접 의존은 지양

## 검증 방법

변경한 파일의 import 문을 확인하여 아래 패턴에 해당하면 위반이다.

**위반 예시:**
```kotlin
// ❌ Service에서 JpaRepository 직접 사용
package co.yappuworld.user.client.application
import co.yappuworld.user.infrastructure.jpa.UserRepository  // 위반

// ❌ Controller에서 infrastructure 직접 참조
package co.yappuworld.user.client.presentation
import co.yappuworld.user.infrastructure.UserFindService  // 위반
```

**올바른 예시:**
```kotlin
// ✅ Service에서 FindService/CommandService 사용
package co.yappuworld.user.client.application
import co.yappuworld.user.infrastructure.UserFindService  // 정상

// ✅ Controller에서 application Service만 사용
package co.yappuworld.user.client.presentation
import co.yappuworld.user.client.application.UserService  // 정상
```
