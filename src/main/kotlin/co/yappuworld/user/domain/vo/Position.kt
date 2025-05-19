package co.yappuworld.user.domain.vo

enum class Position(
    val label: String
) {
    PM("PM"),
    DESIGN("Design"),
    WEB("Web"),
    ANDROID("Android"),
    IOS("iOS"),
    FLUTTER("Flutter"),
    SERVER("Server"),
    STAFF("운영진");

    companion object {
        val participantPositions: List<Position> = listOf(PM, DESIGN, WEB, ANDROID, IOS, FLUTTER, SERVER)
    }

    fun isStaff(): Boolean = this == STAFF

    fun isParticipantPosition(): Boolean = this != STAFF
}
