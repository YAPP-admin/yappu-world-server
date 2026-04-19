package co.yappuworld.user.infrastructure.lock

interface SignUpEmailLockExecutor {

    fun <T> executeWithLockReturning(
        email: String,
        action: () -> T
    ): T
}
