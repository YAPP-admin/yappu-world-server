package co.yappuworld.team.domain.vo

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class TeamError : Error {

    TEAM_NOT_FOUND {
        override val message: String = "팀을 찾을 수 없습니다."
        override val code: String = "TEAM_0001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    SERVICE_NOT_FOUND {
        override val message: String = "서비스를 찾을 수 없습니다."
        override val code: String = "TEAM_0002"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },

    TEAM_ALREADY_EXISTS {
        override val message: String = "팀이 이미 존재합니다."
        override val code: String = "TEAM_1001"
        override val type: ErrorType = ErrorType.WRONG_STATE
    }
}
