package co.yappuworld.operation.application.dto.response

import co.yappuworld.operation.application.dto.param.GenerationActivationControlResult

data class AdminGenerationActiveUpdateAppResponseDto(
    val activatedGeneration: Int? = null,
    val deactivatedGeneration: Int? = null
) {

    constructor(response: GenerationActivationControlResult) : this(
        activatedGeneration = response.activatedGeneration,
        deactivatedGeneration = response.deactivatedGeneration
    )
}
