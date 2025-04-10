package co.yappuworld.attendance.domain

/**
 * @property PRESENT 제 시간에 정상적으로 출석
 * @property EXCUSED_ABSENCE 출석으로 인정되는 결석
 */
enum class AttendanceStatus(
    label: String
) {
    PRESENT("출석"),
    LATE("지각"),
    ABSENT("결석"),
    LEFT_EARLY("조퇴"),
    EXCUSED_ABSENCE("공결")
}
