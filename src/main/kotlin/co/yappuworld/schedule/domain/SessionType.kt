package co.yappuworld.schedule.domain

enum class SessionType(
    val label: String
) {
    OFFLINE("오프라인"),
    ONLINE("온라인"),
    TEAM("팀세션")
}
