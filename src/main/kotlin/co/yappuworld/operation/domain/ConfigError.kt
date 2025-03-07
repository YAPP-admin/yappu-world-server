package co.yappuworld.operation.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class ConfigError : Error {

    WRONG_VERSION_FORMAT {
        override val message = "올바르지 않은 버전 형태입니다. 버전은 x.y.z 형태여야 합니다."
        override val code = "CFG_0001"
        override val type = ErrorType.WRONG_ARGUMENT
    },

    CONFIG_KEY_ERROR {
        override val message = "환경 변수 세팅에 오류가 발생했습니다. 요청 데이터를 확인해주세요."
        override val code = "CFG_9000"
        override val type = ErrorType.WRONG_ARGUMENT
    }
}
