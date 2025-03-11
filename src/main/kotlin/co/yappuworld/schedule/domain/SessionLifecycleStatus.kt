package co.yappuworld.schedule.domain

enum class SessionLifecycleStatus(
    private val label: String
) {
    DONE("완료"),
    TODAY("당일"),
    UPCOMING("임박"),
    PENDING("예정")
}
