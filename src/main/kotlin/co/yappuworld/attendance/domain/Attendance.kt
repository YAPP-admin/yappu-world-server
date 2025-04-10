package co.yappuworld.attendance.domain

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "attendances")
class Attendance(
    status: AttendanceStatus,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(name = "schedule_id", nullable = false)
    val scheduleId: UUID
) : BaseEntity() {

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AttendanceStatus = status
        private set

    @Column(name = "checked_at", nullable = true)
    var checkedAt: LocalDateTime? = null
        private set
}
