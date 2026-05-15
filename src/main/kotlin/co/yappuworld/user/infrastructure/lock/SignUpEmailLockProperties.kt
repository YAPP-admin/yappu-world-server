package co.yappuworld.user.infrastructure.lock

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.lock.signup-email")
data class SignUpEmailLockProperties(
    val timeoutSeconds: Long = 3L
)
