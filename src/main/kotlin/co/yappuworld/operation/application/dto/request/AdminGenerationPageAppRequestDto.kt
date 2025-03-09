package co.yappuworld.operation.application.dto.request

import org.springframework.data.domain.PageRequest

data class AdminGenerationPageAppRequestDto(
    val page: Int,
    val limit: Int
) {

    fun toPageRequest(): PageRequest =
        PageRequest.of(
            page - 1,
            limit
        )
}
