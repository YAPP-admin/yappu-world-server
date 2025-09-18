package co.yappuworld.global.config

import co.yappuworld.external.discord.DiscordProperties
import co.yappuworld.external.fcm.FcmProperties
import co.yappuworld.external.map.KakaoProperties
import co.yappuworld.global.property.AdminProperties
import co.yappuworld.global.security.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    JwtProperties::class,
    FcmProperties::class,
    DiscordProperties::class,
    AdminProperties::class,
    KakaoProperties::class
)
class AppConfig
