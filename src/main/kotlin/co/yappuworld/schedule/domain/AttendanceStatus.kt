package co.yappuworld.schedule.domain

/**
 * @property ON_TIME 제 시간에 정상적으로 출석
 * @property EXCUSED_ABSENCE 출석으로 인정되는 결석
 */
enum class AttendanceStatus(
    val label: String
) {
    ON_TIME("출석"),
    LATE("지각"),
    ABSENT("결석"),
    EARLY_CHECK_OUT("조퇴"),
    EXCUSED_ABSENCE("공결")
}
