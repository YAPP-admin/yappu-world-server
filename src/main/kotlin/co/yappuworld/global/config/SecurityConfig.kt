package co.yappuworld.global.config

import co.yappuworld.global.filter.JwtFilter
import co.yappuworld.global.security.SecurityPathMatchersManager.staffOrAdminMatchers
import co.yappuworld.global.security.SecurityPathMatchersManager.anyoneMatchers
import co.yappuworld.global.security.SecurityPathMatchersManager.userMatchers
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { getCorsConfigure() }
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
            }.authorizeHttpRequests { it.anyRequest().permitAll() }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    private fun getCorsConfigure(): CorsConfiguration =
        CorsConfiguration().apply {
            addAllowedHeader("*")
            addAllowedMethod(GET)
            addAllowedMethod(POST)
            addAllowedMethod(PUT)
            addAllowedMethod(DELETE)
            addAllowedOriginPattern("*")
        }
}
