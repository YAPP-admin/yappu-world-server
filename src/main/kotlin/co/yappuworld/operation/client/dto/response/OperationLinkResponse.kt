package co.yappuworld.operation.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class OperationLinkResponse(
    @Schema(
        description = "URL",
        nullable = true
    )
    val link: String?
)
