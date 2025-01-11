package co.yappuworld.global.exception

class BusinessException private constructor(
    val error: Error,
    val e: Throwable?
) : RuntimeException(error.message, e) {

    constructor(error: Error) : this(error, null)
}
