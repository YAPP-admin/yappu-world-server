package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
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

    companion object {

        fun checkInSession(
            now: LocalDateTime,
            userId: UUID,
            session: SessionEntity
        ): AttendanceEntity {
            val isNowBetweenLateTimeRange =
                session.lateTimeFrom.isBeforeOrEqual(now) && now.isBeforeOrEqual(session.lateTimeUntil)

            val status = when {
                now < session.lateTimeFrom -> AttendanceStatus.ON_TIME
                isNowBetweenLateTimeRange -> AttendanceStatus.LATE
                else -> AttendanceStatus.ABSENT
            }

            return AttendanceEntity(
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
