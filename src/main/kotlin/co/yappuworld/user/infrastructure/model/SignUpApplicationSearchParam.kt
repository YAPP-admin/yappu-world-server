package co.yappuworld.user.infrastructure.model

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.SignUpApplicationStatus

data class SignUpApplicationSearchParam(
    val name: String?,
    val status: SignUpApplicationStatus?,
    val generation: Int?,
    val position: Position?
)
