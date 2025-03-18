package co.yappuworld.board.client.application.dto.response

import co.yappuworld.user.domain.vo.Position

data class BoardActivityUnitAppResponseDto(
    val generation: Int,
    val position: Position
)
