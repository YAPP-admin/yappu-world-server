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
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    INVALID_TEAM_MEMBERS {
        override val message: String = "유효하지 않은 팀원 정보입니다."
        override val code: String = "TEAM_1002"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    INVALID_DELETE_REQUEST {
        override val message: String = "삭제 요청이 유효하지 않습니다."
        override val code: String = "TEAM_1003"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    INVALID_ACTIVITY_UNIT {
        override val message: String = "존재하지 않는 활동 이력 ID입니다."
        override val code: String = "TEAM_1004"
        override val type: ErrorType = ErrorType.WRONG_STATE
    }
}
