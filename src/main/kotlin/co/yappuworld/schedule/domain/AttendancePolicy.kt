package co.yappuworld.schedule.domain

object AttendancePolicy {
    // TIME
    const val CHECK_IN_AVAILABLE_BEFORE_SESSION_START_MINUTES = 20L
    const val LATE_AFTER_SESSION_START_MINUTES = 20L
    const val ABSENT_AFTER_SESSION_START_HOURS = 2L

    // POINT
    const val ATTENDANCE_DEFAULT_POINT = 100
    const val LATE_PENALTY = 10
    const val ABSENT_PENALTY = 20
    const val LATE_PASS_BONUS = 10
}
