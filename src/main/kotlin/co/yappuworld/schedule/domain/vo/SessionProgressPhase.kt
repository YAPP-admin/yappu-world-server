package co.yappuworld.schedule.domain.vo

enum class SessionProgressPhase(
    val label: String
) {
    DONE("완료"),
    ONGOING("진행 중"),
    TODAY("당일"),
    PENDING("예정")
}
