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
                    operation.responses.addCommonResponse()
                }
            }
        }

    private fun ApiResponses.addCommonResponse() {
        for ((status, response) in this) {
            if (response.content == null) {
                continue
            }

            val commonResponse = SwaggerCommonResponses.v[status]
            when (commonResponse == null) {
                true -> {
                    val examples = mutableMapOf<String, Example>().apply {
                        response.content.values.forEach { mediaType ->
                            mediaType.examples?.let { putAll(it) }
                            mediaType.example?.let {
                                put("성공", Example().apply { value = it })
                            }
                        }
                    }

                    val content = Content().apply {
                        addMediaType(
                            "application/json",
                            MediaType().apply { setExamples(examples) }
                        )
                    }

                    response.content = content
                }
                false -> response.combineContent(commonResponse)
            }
        }

        for ((status, response) in SwaggerCommonResponses.v) {
            if (this[status] == null) {
                this.addApiResponse(status, response)
            }
        }
    }

    private fun ApiResponse.combineContent(response: ApiResponse): ApiResponse {
        val examples = mutableMapOf<String, Example>()
        this.content.values.forEach { value ->
            if (value.examples != null) {
                examples.putAll(value.examples)
            }
        }
        response.content.values.forEach {
            if (it.examples != null) {
                examples.putAll(it.examples)
            }
        }

        this.content = Content().apply {
            addMediaType(
                "application/json",
                MediaType().apply { setExamples(examples) }
            )
        }
        return this
    }
}
