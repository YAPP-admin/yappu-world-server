package co.yappuworld.global.security.error

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class TokenError : Error {
    EXPIRED_TOKEN {
        override val message: String = "만료된 토큰입니다."
        override val code: String = "TKN_0001"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    INVALID_TOKEN {
        override val message: String = "비정상 토큰입니다."
        override val code: String = "TKN_0002"
        override val type: ErrorType = ErrorType.WRONG_STATE
    }
}
