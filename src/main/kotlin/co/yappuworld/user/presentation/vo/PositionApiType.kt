package co.yappuworld.user.presentation.vo

import co.yappuworld.user.domain.Position

enum class PositionApiType {
    PM,
    DESIGN,
    WEB,
    ANDROID,
    IOS,
    SERVER;

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
