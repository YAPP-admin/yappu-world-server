package co.yappuworld.user.domain.vo

/**
 * @property ADMIN 관리자
 * @property STAFF 운영진
 * @property ALUMNI 정회원
 * @property GRADUATE 수료회원
 * @property ACTIVE 활동회원
 */
enum class UserRole(
    val authority: String,
    val label: String,
    val authenticationCodeKey: String
) {
    ADMIN("ROLE_ADMIN", "관리자", "authenticationCodeAdmin"),
    STAFF("ROLE_STAFF", "운영진", "authenticationCodeStaff"),
    ALUMNI("ROLE_ALUMNI", "정회원", "authenticationCodeAlumni"),
    GRADUATE("ROLE_GRADUATE", "수료회원", "authenticationCodeGraduate"),
    ACTIVE("ROLE_ACTIVE", "활동회원", "authenticationCodeActive")
}
