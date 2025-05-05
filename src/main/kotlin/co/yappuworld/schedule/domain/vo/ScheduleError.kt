package co.yappuworld.schedule.domain.vo

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
    },
    CONTAIN_IMPROPER_ID_FOR_DELETE_SESSION {
        override val message: String = "삭제할 수 없는 ID가 포함되어 있습니다."
        override val code: String = "SCH_1004"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    NO_UPCOMING_SESSION {
        override val message: String = "예정된 세션이 존재하지 않습니다."
        override val code: String = "SCH_1005"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    NO_SESSION_WITHOUT_ACTIVE_GENERATION {
        override val message: String = "활성화 된 기수가 없어서 임박한 세션이 존재하지 않습니다."
        override val code: String = "SCH_1006"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },

    // 4000번대 - 일정 공통
    START_DATETIME_AFTER_END_DATETIME {
        override val message: String = "시작 시간이 종료 시간보다 늦을 수 없습니다."
        override val code: String = "SCH_4000"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    }
}
