package co.yappuworld.operation.client.dto.response

import co.yappuworld.operation.domain.ConfigEntity
import io.swagger.v3.oas.annotations.media.Schema

data class AdminOperationLinksResponse(
    @Schema(description = "링크 목록")
    val links: List<AdminOperationLinkResponse>
) {

    companion object {
        fun from(configs: List<ConfigEntity>): AdminOperationLinksResponse =
            AdminOperationLinksResponse(
                configs
                    .map { AdminOperationLinkResponse(it) }
                    .sortedBy { it.id }
            )
    }
}

data class AdminOperationLinkResponse(
    @Schema(description = "링크 식별자")
    val id: String,
    @Schema(description = "링크 이름")
    val label: String,
    @Schema(description = "링크 URL", nullable = true)
    val value: String?
) {

    constructor(config: ConfigEntity) : this(
        id = config.id,
        label = config.label,
        value = config.value
    )
}
