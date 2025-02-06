package co.yappuworld.global.config

import co.yappuworld.external.discord.DiscordProperty
import co.yappuworld.external.fcm.FcmProperty
import co.yappuworld.global.security.JwtProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperty::class, FcmProperty::class, DiscordProperty::class)
class AppConfig
