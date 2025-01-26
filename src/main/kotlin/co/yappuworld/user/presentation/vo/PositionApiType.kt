package co.yappuworld.user.presentation.vo

import co.yappuworld.user.domain.vo.Position

enum class PositionApiType(
    val label: String
) {
    PM("PM"),
    DESIGN("Design"),
    WEB("Web"),
    ANDROID("Android"),
    IOS("iOS"),
    SERVER("Server");

    fun toDomainType(): Position {
        return when (this) {
            PM -> Position.PM
            DESIGN -> Position.DESIGN
            WEB -> Position.WEB
            ANDROID -> Position.ANDROID
            IOS -> Position.IOS
            SERVER -> Position.SERVER
        }
    }
}
