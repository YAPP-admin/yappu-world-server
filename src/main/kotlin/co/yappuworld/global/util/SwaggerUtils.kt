package co.yappuworld.global.util

import co.yappuworld.global.config.SwaggerCommonResponses
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
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
            mediaType.examples?.let { newExamples.putAll(it) }
        }

        SwaggerCommonResponses.errorResponses[status]?.let { errorResponse ->
            errorResponse.content.values.forEach { mediaType ->
                mediaType.examples?.let { newExamples.putAll(it) }
            }
        }

        replaceWith(newExamples, "#/components/schemas/ErrorResponse")

        return this
    }

    private fun ApiResponse.refineResponse() {
        val refs = mutableListOf<String>()
        val examples = mutableMapOf<String, Example>().apply {
            content.values.forEach { mediaType ->
                mediaType.examples?.let { putAll(it) }
                mediaType.example?.let { put("성공", Example().apply { value = it }) }
                refs.addAll(collectAllRefs(mediaType.schema))
            }
        }

        replaceWith(
            examples,
            refs.maxByOrNull { it.length }
        )
    }

    private fun ApiResponse.replaceWith(
        examples: Map<String, Example>,
        schemaRef: String? = null
    ) {
        this.content = Content().apply {
            addMediaType(
                "application/json",
                MediaType().apply {
                    schema = Schema<String>(SpecVersion.V30).apply { `$ref` = schemaRef }
                    setExamples(examples)
                }
            )
        }
    }

    private fun collectAllRefs(schema: Schema<*>?): Set<String> {
        val refs = mutableSetOf<String>()
        val visited = mutableSetOf<Schema<*>>() // 순환 참조 방지

        fun traverse(current: Schema<*>?) {
            if (current == null || current in visited) return
            visited += current

            // ref가 있으면 더 이상 내려가지 않음
            current.`$ref`?.let {
                refs += it
                return
            }

            current.items?.let { traverse(it) }
            current.allOf?.forEach(::traverse)
            current.anyOf?.forEach(::traverse)
            current.oneOf?.forEach(::traverse)
            current.properties?.values?.forEach(::traverse)
            val additional = current.additionalProperties
            if (additional is Schema<*>) traverse(additional)
        }

        traverse(schema)
        return refs
    }
}
