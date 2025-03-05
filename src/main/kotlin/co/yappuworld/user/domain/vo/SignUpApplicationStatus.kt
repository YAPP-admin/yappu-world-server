package co.yappuworld.user.domain.vo

/**
 * @property PENDING 신청상태
 * @property APPROVED 신청승인
 * @property REJECTED 신청거절
 */
enum class SignUpApplicationStatus(
    val label: String
) {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거절")
}
