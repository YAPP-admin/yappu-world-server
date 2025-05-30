package co.yappuworld.user.domain.vo

enum class Position(
    val label: String,
    val order: Int
) {
    PM("PM", 0),
    DESIGN("Design", 1),
    WEB("Web", 2),
    ANDROID("Android", 3),
    IOS("iOS", 4),
    FLUTTER("Flutter", 5),
    SERVER("Server", 6),
    STAFF("운영진", 7);

    fun isStaff(): Boolean = this == STAFF

    fun isAttendeePosition(): Boolean = this != STAFF

    companion object {
        val attendeePositions = listOf(
            PM,
            DESIGN,
            WEB,
            ANDROID,
            IOS,
            FLUTTER,
            SERVER
        )
    }
}
