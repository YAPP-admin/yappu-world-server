package co.yappuworld.schedule.domain

enum class ScheduleProgressPhase(
    private val label: String
) {
    DONE("완료"),
    TODAY("오늘"),
    ONGOING("진행 중"),
    PENDING("예정")
}
