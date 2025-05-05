package co.yappuworld.schedule.domain.vo

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
    NOT_CHECK_IN_TIME {
        override val message: String = "출석 체크를 할 수 있는 시간이 아닙니다."
        override val code: String = "ATD_1002"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    NO_ATTENDANCE_TO_CHECK_IN {
        override val message: String = "출석이 불가합니다. 개발자에게 문의해주세요."
        override val code: String = "ATD_1003"
        override val type: ErrorType = ErrorType.UNEXPECTED_ERROR
    },
    UNREGISTERED_ATTENDANCE_CODE {
        override val message: String = "출석코드가 등록되지 않았습니다."
        override val code: String = "ATD_1004"
        override val type: ErrorType = ErrorType.WRONG_STATE
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
    UNAUTHORIZED_CHECK_IN {
        override val message: String = "출석할 수 있는 권한이 없습니다."
        override val code: String = "ATD_2004"
        override val type: ErrorType = ErrorType.FORBIDDEN
    },
    USER_NOT_FOUND {
        override val message: String = "유저의 출석 정보를 찾을 수 없습니다."
        override val code: String = "ATD_2005"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },

    // 4000번대 - 어드민
    CANNOT_UPDATE_STATUS {
        override val message: String = "출석 상태를 변경할 수 없습니다."
        override val code: String = "ATD_4000"
        override val type: ErrorType = ErrorType.WRONG_STATE
    }
}
