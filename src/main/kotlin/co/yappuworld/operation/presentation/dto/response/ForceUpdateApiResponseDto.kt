package co.yappuworld.operation.presentation.dto.response

import co.yappuworld.operation.domain.Config

data class ForceUpdateApiResponseDto(
    val needForceUpdate: Boolean,
    val reason: String?
) {

    companion object {
        fun of(configs: List<Config>): ForceUpdateApiResponseDto {
            val configByKey = configs.associateBy { it.id }
            return when (configByKey["needForceUpdate"]?.value == "true") {
                true -> ForceUpdateApiResponseDto(true, configByKey["forceUpdateReason"]?.value)
                false -> ForceUpdateApiResponseDto(false, null)
            }
        }
    }
}
