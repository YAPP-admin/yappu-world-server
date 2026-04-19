package co.yappuworld.global.config

import co.yappuworld.external.messenger.DiscordProperties
import co.yappuworld.external.fcm.FcmProperties
import co.yappuworld.external.map.KakaoProperties
import co.yappuworld.global.property.AdminProperties
import co.yappuworld.global.security.JwtProperties
import co.yappuworld.user.infrastructure.lock.SignUpEmailLockProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    JwtProperties::class,
    FcmProperties::class,
    DiscordProperties::class,
    AdminProperties::class,
    KakaoProperties::class,
    SignUpEmailLockProperties::class
)
class AppConfig
