package co.yappuworld.global.security

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.security.web.util.matcher.RequestMatchers

object SecurityPathMatchersManager {
    val anyoneMatchers = RequestMatchers.anyOf(
        antMatcher("/health"),
        // swagger
        antMatcher("/swagger-ui/**"),
        antMatcher("/v3/api-docs/**"),
        // api
        antMatcher(POST, "/v1/auth/sign-up"),
        antMatcher(GET, "/v1/positions")
    )

    val adminMatchers = RequestMatchers.anyOf(
        antMatcher(POST, "/v1/admin/**"),
        antMatcher(POST, "/v1/admin/auth/application/reject")
    )
}
