package co.yappuworld.schedule.domain.vo

enum class SessionProgressPhase(
    val label: String,
    val order: Int
) {
    DONE("종료", 0),
    ONGOING("진행 중", 1),
    TODAY("당일", 2),
    PENDING("예정", 3)
}
