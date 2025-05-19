package co.yappuworld.schedule.infrastructure.jpa

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "session_participants")
class SessionParticipantEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    val session: SessionEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    val activityUnit: ActivityUnitEntity
) : BaseEntity() {

    @Column(name = "attendance_status")
    @Enumerated(value = EnumType.STRING)
    var attendanceStatus: AttendanceStatus? = null
        private set

    @Column(name = "checked_in_at")
    var checkedInAt: LocalDateTime? = null

    fun checkIn(now: LocalDateTime) {
        val isNowBetweenLateTimeRange =
            session.lateTimeFrom.isBeforeOrEqual(now) && now.isBeforeOrEqual(session.lateTimeUntil)

        this.checkedInAt = now
        this.attendanceStatus = when {
            now < session.lateTimeFrom -> AttendanceStatus.ON_TIME
            isNowBetweenLateTimeRange -> AttendanceStatus.LATE
            else -> AttendanceStatus.ABSENT
        }
    }

    fun adminUpdate(attendanceStatus: AttendanceStatus) {
        this.attendanceStatus = attendanceStatus
    }
}
