package co.yappuworld.user.application.dto.request

import org.springframework.data.domain.PageRequest

data class AdminSignUpApplicationPageAppRequestDto(
    val page: Int,
    val size: Int
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}
