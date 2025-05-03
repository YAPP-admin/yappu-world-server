package co.yappuworld.global.config

import co.yappuworld.global.filter.JwtFilter
import co.yappuworld.global.security.SecurityPathMatchersManager.anyoneMatchers
import co.yappuworld.global.security.SecurityPathMatchersManager.staffOrAdminMatchers
import co.yappuworld.global.security.SecurityPathMatchersManager.userMatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(getCorsConfigureSource()) }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(anyoneMatchers)
                    .permitAll()
                    .requestMatchers(userMatchers)
                    .hasAnyRole("ADMIN", "STAFF", "ALUMNI", "GRADUATE", "ACTIVE")
                    .requestMatchers(staffOrAdminMatchers)
                    .hasAnyRole("ADMIN", "STAFF")
                    .anyRequest()
                    .permitAll()
            }.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    private fun getCorsConfigureSource(): CorsConfigurationSource =
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/**",
                CorsConfiguration().apply {
                    allowedHeaders = listOf("*")
                    allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    allowedOriginPatterns = listOf("*")
                }
            )
        }
}
