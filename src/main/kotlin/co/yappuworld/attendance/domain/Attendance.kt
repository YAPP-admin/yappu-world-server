package co.yappuworld.attendance.domain

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.schedule.domain.SessionEntity
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

    companion object {

        fun checkInSession(
            now: LocalDateTime,
            userId: UUID,
            session: SessionEntity
        ): Attendance {
            val status = when {
                now < session.lateTimeFrom -> AttendanceStatus.ON_TIME
                now in session.lateTimeRange -> AttendanceStatus.LATE
                else -> AttendanceStatus.ABSENT
            }

            return Attendance(
                status = status,
                userId = userId,
                scheduleId = session.id
            )
        }
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AttendanceStatus = status
        private set

    fun updateStatus(status: AttendanceStatus) {
        this.status = status
    }
}
