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

    companion object {
        val participantPositions: List<Position> = listOf(PM, DESIGN, WEB, ANDROID, IOS, FLUTTER, SERVER)

        fun labelOf(label: String): Position =
            Position.entries.singleOrNull { it.label == label }
                ?: throw IllegalArgumentException("Invalid position label: $label")
    }

    fun isStaff(): Boolean = this == STAFF

    fun isParticipantPosition(): Boolean = this != STAFF
}
