package co.yappuworld.user.infrastructure.model

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * 읽기 전용입니다.
 * created_at과 updated_at을 초기화 하지 않아서, 저장에 사용하면 예외 발생합니다.
 */
data class UserWithLastActivityUnit(
    private val _userId: String,
    val email: String,
    val name: String,
    private val _role: String,
    val isActive: Boolean,
    private val _createdAt: Timestamp,
    val generation: Int,
    private val _position: String,
    private val _activityUnitId: String
) {

    val userId: UUID
        get() = UUID.fromString(_userId)

    val role: UserRole
        get() = UserRole.valueOf(_role)

    val createdAt: LocalDateTime
        get() = _createdAt.toLocalDateTime()

    val position: Position
        get() = Position.valueOf(_position)

    val activityUnitId: UUID
        get() = UUID.fromString(_activityUnitId)
}
