package co.yappuworld.user.domain

/**
 * @property ADMIN 관리자
 * @property ALUMNI 정회원
 * @property GRADUATE 수료회원
 * @property ACTIVE 활동회원
 */
enum class UserRole(
    val authority: String
) {
    ADMIN("ROLE_ADMIN"),
    ALUMNI("ROLE_ALUMNI"),
    GRADUATE("ROLE_GRADUATE"),
    ACTIVE("ROLE_ACTIVE")
}
