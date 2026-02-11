package co.yappuworld.operation.client.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class OperationLinkResponse(
    @param:Schema(description = "URL", nullable = true, example = "https://yapp.co.kr")
    val link: String?
)
