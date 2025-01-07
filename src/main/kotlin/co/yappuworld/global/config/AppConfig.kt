package co.yappuworld.global.config

import co.yappuworld.global.property.JwtProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperty::class)
class AppConfig
