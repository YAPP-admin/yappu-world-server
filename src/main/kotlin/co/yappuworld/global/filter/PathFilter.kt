package co.yappuworld.global.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class PathFilter(
    private val pathRegistry: PathRegistry
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        if (!pathRegistry.isAccessiblePath(path)) {
            response.apply {
                status = HttpServletResponse.SC_NOT_FOUND
                writer.write("Not Found")
            }
            return
        }

        filterChain.doFilter(request, response)
    }
}
