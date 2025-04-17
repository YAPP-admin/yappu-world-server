package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class AttendanceError : Error {

    ATTENDANCE_CODE_NOT_FOUND {
        override val message: String = "출석 코드를 찾을 수 없습니다."
        override val code: String = "ATD_0001"
        override val type: ErrorType = ErrorType.UNEXPECTED_ERROR
    },
    ALREADY_CHECKED_IN {
        override val message: String = "이미 출석 체크를 하였습니다."
        override val code: String = "ATD_1000"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    ATTENDANCE_CODE_NOT_MATCH {
        override val message: String = "출석 코드가 일치하지 않습니다."
        override val code: String = "ATD_1001"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },

    // 외부 도메인 에러
    SESSION_NOT_FOUND {
        override val message: String = "출석 대상 세션을 찾을 수 없습니다."
        override val code: String = "ATD_2000"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    CHECK_IN_ONLY_FOR_SESSION {
        override val message: String = "출석은 세션 타입 일정에만 할 수 있습니다."
        override val code: String = "ATD_2001"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    NO_ACTIVE_GENERATION {
        override val message: String = "활성화 된 기수가 없어서 출석 관련 처리가 불가합니다."
        override val code: String = "ATD_2002"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    GENERATION_NOT_MATCH {
        override val message: String = "출석 처리를 위한 기수 정보가 올바르지 않습니다."
        override val code: String = "ATD_2003"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    USER_NOT_ACTIVATE {
        override val message: String = "활동 유저가 아니라서 출석이 불가합니다."
        override val code: String = "ATD_2004"
        override val type: ErrorType = ErrorType.FORBIDDEN
    }
}
