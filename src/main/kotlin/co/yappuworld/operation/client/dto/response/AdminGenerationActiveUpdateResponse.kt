package co.yappuworld.operation.client.dto.response

import co.yappuworld.operation.client.dto.param.GenerationActivationControlResult
import io.swagger.v3.oas.annotations.media.Schema

data class AdminGenerationActiveUpdateResponse(
    @Schema(description = "활성화 된 기수", nullable = true)
    val activatedGeneration: Int? = null,
    @Schema(description = "비활성화 된 기수", nullable = true)
    val deactivatedGeneration: Int? = null
) {

    constructor(response: GenerationActivationControlResult) : this(
        activatedGeneration = response.activatedGeneration,
        deactivatedGeneration = response.deactivatedGeneration
    )
}
