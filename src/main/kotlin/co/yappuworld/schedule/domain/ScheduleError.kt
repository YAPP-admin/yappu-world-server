package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class ScheduleError : Error {

    // 1000 - 세션
    SESSION_NEED_GENERATION {
        override val message: String = "세션 일정은 기수가 필수입니다."
        override val code: String = "SCH_1000"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    },
    NOT_FOUND_SESSION {
        override val message: String = "세션을 찾지 못했습니다."
        override val code: String = "SCH_1002"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    UPDATE_FAIL_NOT_SESSION_TYPE {
        override val message: String = "세션 타입 수정만 요청 가능합니다."
        override val code: String = "SCH_1003"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    }
}
