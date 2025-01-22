package co.yappuworld.global.exception

enum class GlobalError : Error {
    INTERNAL_SERVER_ERROR {
        override val message: String = "예기치 못한 에러가 발생했습니다. 서버 관리자에게 문의해주세요."
        override val code: String = "COM_0001"
        override val type: ErrorType = ErrorType.UNEXPECTED_ERROR
    },
    INVALID_REQUEST_ARGUMENT {
        override val message: String = "요청된 인자에 잘못된 값이 있습니다."
        override val code: String = "COM_0002"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    }
}
