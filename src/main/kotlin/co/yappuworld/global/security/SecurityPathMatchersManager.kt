package co.yappuworld.global.security

import org.springframework.http.HttpMethod.POST
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.security.web.util.matcher.RequestMatchers

object SecurityPathMatchersManager {
    val anyoneMatchers = RequestMatchers.anyOf(
        antMatcher("/health"),
        // swagger
        antMatcher("/swagger-ui/**"),
        antMatcher("/v3/api-docs/**"),
        antMatcher(POST, "/v1/auth/sign-up"),
        antMatcher(POST, "/v1/admin/auth/application/approve")
    )
}
