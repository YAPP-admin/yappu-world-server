package co.yappuworld.global.security

import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.security.web.util.matcher.RequestMatchers

object SecurityPathMatchersManager {
    val anyoneMatchers: RequestMatcher = RequestMatchers.anyOf(
        antMatcher("/health"),
        // swagger
        antMatcher("/swagger-ui/**"),
        antMatcher("/v3/api-docs/**"),
        // api
        antMatcher(POST, "/v1/auth/sign-up"),
        antMatcher(POST, "/v1/auth/login"),
        antMatcher(POST, "/v1/auth/reissue-token"),
        antMatcher(POST, "/v1/auth/check-email"),
        antMatcher(GET, "/v1/auth/applications/latest"),
        antMatcher(GET, "/v1/positions"),
        antMatcher(GET, "/v1/operations/**")
    )

    val userMatchers: RequestMatcher = RequestMatchers.anyOf(
        antMatcher(DELETE, "/v1/auth/user"),
        antMatcher("/v1/users/fcm"),
        antMatcher("/v1/users/profile"),
        antMatcher(PUT, "/v1/users/fcm"),
        antMatcher(GET, "/v1/alarms"),
        antMatcher(PUT, "/v1/alarms/device"),
        antMatcher(PATCH, "/v1/alarms/master")
    )

    val staffOrAdminMatchers: RequestMatcher = RequestMatchers.anyOf(
        antMatcher(POST, "/admin/**")
    )
}
