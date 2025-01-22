package co.yappuworld.user.domain.model

import co.yappuworld.user.domain.Position

/**
 * @property generation 기수
 * @property position 직군
 */
data class ActivityUnit(
    val generation: Int,
    val position: Position
)
