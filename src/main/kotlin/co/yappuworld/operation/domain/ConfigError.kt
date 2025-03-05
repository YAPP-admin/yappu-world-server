package co.yappuworld.operation.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class ConfigError : Error {

    WRONG_VERSION_FORMAT {
        override val message = "올바르지 않은 버전 형태입니다. 버전은 x.y.z 형태여야 합니다."
        override val code = "CFG_0001"
        override val type = ErrorType.WRONG_ARGUMENT
    }
}
