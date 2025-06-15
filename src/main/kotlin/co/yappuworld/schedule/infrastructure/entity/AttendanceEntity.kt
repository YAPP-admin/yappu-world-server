package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "attendances")
class AttendanceEntity(
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(name = "schedule_id", nullable = false)
    val scheduleId: UUID,
    status: AttendanceStatus = AttendanceStatus.PENDING
) : BaseEntity() {

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AttendanceStatus = status
        private set

    @Column(name = "user_checked_in_at")
    var userCheckedInAt: LocalDateTime? = null
        private set

    fun checkIn(
        status: AttendanceStatus,
        now: LocalDateTime
    ) {
        this.status = status
        this.userCheckedInAt = now
    }

    fun updateStatus(status: AttendanceStatus) {
        this.status = status
    }

    fun hasAttended(): Boolean = status != AttendanceStatus.PENDING

    fun canAttend(): Boolean = status == AttendanceStatus.PENDING
}
