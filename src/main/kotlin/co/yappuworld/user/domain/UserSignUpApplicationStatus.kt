package co.yappuworld.user.domain

/**
 * @property PENDING 신청상태
 * @property APPROVED 신청승인
 * @property REJECTED 신청거절
 */
enum class UserSignUpApplicationStatus {
    PENDING,
    APPROVED,
    REJECTED
}
