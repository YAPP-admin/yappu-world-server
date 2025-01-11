package co.yappuworld.global.config

import co.yappuworld.global.security.SecurityPathMatchersManager.anyoneMatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { getCorsConfigure() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .authorizeHttpRequests { it.requestMatchers(anyoneMatchers).permitAll() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
    }

    private fun getCorsConfigure(): CorsConfiguration {
        return CorsConfiguration().apply {
            addAllowedHeader("*")
            addAllowedMethod(GET)
            addAllowedMethod(POST)
            addAllowedMethod(PUT)
            addAllowedMethod(DELETE)
            addAllowedOriginPattern("*")
        }
    }
}
