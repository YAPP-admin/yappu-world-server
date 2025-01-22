package co.yappuworld.user.domain.vo

/**
 * @property ADMIN 관리자
 * @property ALUMNI 정회원
 * @property GRADUATE 수료회원
 * @property ACTIVE 활동회원
 */
enum class UserRole(
    val authority: String,
    val label: String
) {
    ADMIN("ROLE_ADMIN", "관리자"),
    ALUMNI("ROLE_ALUMNI", "정회원"),
    GRADUATE("ROLE_GRADUATE", "수료회원"),
    ACTIVE("ROLE_ACTIVE", "활동회원")
}
