package co.yappuworld.global.util

import co.yappuworld.global.config.SwaggerCommonResponses
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OpenApiCustomizer

object SwaggerUtils {

    val customizeResponses: OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.paths.values.forEach { item ->
                item.readOperations().forEach { operation ->
                    operation.responses.appendAndOrganizeResponse()
                }
            }
        }

    private fun ApiResponses.appendAndOrganizeResponse() {
        // API에 명시된 응답을 순회
        for ((status, response) in this) {
            if (response.content == null) {
                continue
            }

            when (SwaggerCommonResponses.hasErrorResponse(status)) {
                true -> response.addCommonErrorResponse(status)
                false -> response.refineResponse()
            }
        }

        // Error를 순회하며 추가되지 않은 공통 에러 응답을 추가
        for ((status, response) in SwaggerCommonResponses.errorResponses) {
            if (this[status] == null) {
                this.addApiResponse(status, response)
            }
        }
    }

    private fun ApiResponse.addCommonErrorResponse(status: String): ApiResponse {
        val newExamples = mutableMapOf<String, Example>()

        this.content.values.forEach { mediaType ->
            mediaType.example?.let { newExamples["성공"] = Example().apply { value = it } }
            mediaType.examples?.let { newExamples.putAll(it) }
        }

        SwaggerCommonResponses.errorResponses[status]?.let { errorResponse ->
            errorResponse.content.values.forEach { mediaType ->
                mediaType.examples?.let { newExamples.putAll(it) }
            }
        }

        replaceWith(newExamples)

        return this
    }

    private fun ApiResponse.refineResponse() =
        replaceWith(
            mutableMapOf<String, Example>().apply {
                content.values.forEach { mediaType ->
                    mediaType.examples?.let { putAll(it) }
                    mediaType.example?.let { put("성공", Example().apply { value = it }) }
                }
            }
        )

    private fun ApiResponse.replaceWith(examples: Map<String, Example>) {
        this.content = Content().apply {
            addMediaType(
                "application/json",
                MediaType().apply { setExamples(examples) }
            )
        }
    }
}
