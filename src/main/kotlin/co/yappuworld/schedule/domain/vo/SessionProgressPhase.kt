package co.yappuworld.schedule.domain.vo

enum class SessionProgressPhase(
    val label: String
) {
    DONE("완료"),
    TODAY("당일"),
    UPCOMING("임박"),
    PENDING("예정")
}
