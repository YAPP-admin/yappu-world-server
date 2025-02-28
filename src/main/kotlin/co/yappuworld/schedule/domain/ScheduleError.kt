package co.yappuworld.schedule.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class ScheduleError : Error {

    // 1000 - 세션
    SESSION_NEED_GENERATION {
        override val message: String = "세션 타입의 일정은 기수가 필수입니다."
        override val code: String = "SCH_1000"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    }
}
