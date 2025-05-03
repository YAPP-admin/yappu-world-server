package co.yappuworld.global.config

import co.yappuworld.global.util.SwaggerUtils.customizeResponses
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    private val securitySchemeName = "JWT Authorization"

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(getInfo())
            .addSecurityItem(getSecurityRequirement())
            .components(Components().addSecuritySchemes(securitySchemeName, getSecurityScheme()))

    @Bean
    fun appApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("App API")
            .pathsToMatch("/v1/**")
            .addOpenApiCustomizer(customizeResponses)
            .build()

    @Bean
    fun adminApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("Admin API")
            .pathsToMatch("/admin/**") // 어드민 API만 포함
            .addOpenApiCustomizer(customizeResponses)
            .build()

    private fun getInfo(): Info =
        Info()
            .title("Yappu World API")
            .description("YAPP 공식 APP 서버 스웨거\n ")
            .contact(getContact())
            .version("0.0.1")

    private fun getContact(): Contact =
        Contact()
            .name("Github Repository")
            .url("https://github.com/YAPP-admin/yappu-world-server")

    private fun getSecurityRequirement(): SecurityRequirement = SecurityRequirement().addList(securitySchemeName)

    private fun getSecurityScheme(): SecurityScheme =
        SecurityScheme()
            .name(securitySchemeName)
            .type(SecurityScheme.Type.HTTP)
            .`in`(SecurityScheme.In.HEADER)
            .scheme("Bearer")
            .bearerFormat("JWT")
}
