package co.yappuworld.global.filter

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class PathRegistry(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) {

    lateinit var registeredPaths: Set<String>
        protected set

    private val matcher = AntPathMatcher()
    private val customRegisteredPaths = setOf(
        "/swagger-ui/**",
        "/health",
        "/v3/api-docs/**",
        "/actuator/**"
    )

    @PostConstruct
    fun init() {
        registeredPaths = requestMappingHandlerMapping.handlerMethods.keys
            .flatMap {
                it.pathPatternsCondition?.patterns?.map { p -> p.patternString } ?: emptySet()
            }.union(customRegisteredPaths)
            .toSet()
    }

    fun isAccessiblePath(path: String): Boolean =
        registeredPaths.any { pattern ->
            matcher.match(pattern, path)
        }
}
