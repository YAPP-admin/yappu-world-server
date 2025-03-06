package co.yappuworld.user.infrastructure.model

import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.time.LocalDateTime
import java.util.UUID

/**
 * 읽기 전용입니다.
 * created_at과 updated_at을 초기화 하지 않아서, 저장에 사용하면 예외 발생합니다.
 */
data class UserWithLastActivityUnit(
    val userId: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    private val generation: Int,
    private val position: Position,
    private val activityUnitId: UUID
) {

    val activityUnit = ActivityUnit(
        generation = generation,
        position = position,
        userId = userId
    ).withId(activityUnitId)
}
