package co.yappuworld.operation.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class OperationError : Error {

    // 1000 - Generation (기수)
    NOT_EXIST_GENERATION {
        override val message: String = "존재하지 않는 기수입니다."
        override val code: String = "OPR_1000"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    },
    GENERATION_INCONSISTENCY {
        override val message: String = "기수 활성상태의 정합성이 맞지 않습니다. 데이터를 확인해주세요."
        override val code: String = "OPR_1001"
        override val type: ErrorType = ErrorType.WRONG_STATE
    }
}
