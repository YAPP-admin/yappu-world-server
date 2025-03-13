package co.yappuworld.schedule.application.dto.request

import org.springframework.data.domain.PageRequest

data class AdminSessionPageAppRequestDto(
    val page: Int,
    val limit: Int,
    val generation: Int?
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, limit)
}
