package co.yappuworld.operation.presentation.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class OperationLinkApiResponseDto(
    @Schema(
        description = "URL",
        nullable = true
    )
    val link: String?
)
