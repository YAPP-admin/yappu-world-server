package co.yappuworld.global.filter

import co.yappuworld.global.dto.ApiResponse
import co.yappuworld.global.security.JwtHandler
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.error.TokenError
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtHandler: JwtHandler
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            extractToken(request)
        } catch (e: Exception) {
            processTokenException(response, e)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest) {
        if (request.getHeader("Authorization") != null) {
            return
        }

        jwtHandler.extractSecurityUserOrNull(request)?.let {
            saveSecurityUserOnContextHolder(it)
        }
    }

    private fun saveSecurityUserOnContextHolder(securityUser: SecurityUser) {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            securityUser,
            null,
            listOf(SimpleGrantedAuthority(securityUser.role.authority))
        )
    }

    private fun <T : Throwable> processTokenException(
        response: HttpServletResponse,
        e: T
    ) {
        val error = when {
            e is ExpiredJwtException -> TokenError.EXPIRED_TOKEN
            else -> TokenError.INVALID_TOKEN
        }

        logger.warn(error.message, e)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpStatus.UNAUTHORIZED.value()

        ApiResponse(
            isSuccess = false,
            message = error.message,
            errorCode = error.code,
            body = null
        ).let {
            response.writer.write(it.toString())
        }
    }
}
