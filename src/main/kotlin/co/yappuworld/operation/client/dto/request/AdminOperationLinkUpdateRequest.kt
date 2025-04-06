package co.yappuworld.operation.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class AdminOperationLinkUpdateRequest(
    @Schema(description = "링크 식별자", example = "termsOfServiceLink")
    val id: String,
    @Schema(description = "링크 이름", example = "이용약관")
    val name: String,
    @Schema(
        description = "링크 URL",
        example = "https://yapp-workspace.notion.site/48f4eb2ffdd94740979e8a3b37ca260d?pvs=4",
        required = false
    )
    val link: String?
)
