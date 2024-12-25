package co.yappuworld.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    private val securitySchemeName = "JWT Authorization"

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(getInfo())
            .addSecurityItem(getSecurityRequirement())
            .components(Components().addSecuritySchemes(securitySchemeName, getSecurityScheme()))
    }

    private fun getInfo(): Info {
        return Info().title("Yappu World API")
            .description("YAPP 공식 APP 서버 스웨거")
            .version("0.0.1")
    }

    private fun getSecurityRequirement(): SecurityRequirement {
        return SecurityRequirement().addList(securitySchemeName)
    }

    private fun getSecurityScheme(): SecurityScheme {
        return SecurityScheme().name(securitySchemeName)
            .type(SecurityScheme.Type.HTTP)
            .`in`(SecurityScheme.In.HEADER)
            .scheme("Bearer")
            .bearerFormat("JWT")
    }
}
