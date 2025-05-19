package co.yappuworld.schedule.domain.vo

import co.yappuworld.schedule.domain.vo.AttendanceStatus.EXCUSED_ABSENCE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.schedule.infrastructure.jpa.AttendanceEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.user.domain.model.UserWithActivityUnits
import java.time.LocalDateTime

/**
 * @property ON_TIME 제 시간에 정상적으로 출석
 * @property EXCUSED_ABSENCE 출석으로 인정되는 결석
 */
enum class AttendanceStatus(
    val label: String
) {
    ON_TIME("출석"),
    LATE("지각"),
    ABSENT("결석"),
    EARLY_CHECK_OUT("조퇴"),
    EXCUSED_ABSENCE("공결");

    companion object {

        fun from(
            user: UserWithActivityUnits,
            session: SessionEntity,
            attendance: AttendanceEntity?,
            now: LocalDateTime
        ): AttendanceStatus? =
            when {
                attendance != null -> attendance.status
                !user.hasAttendeeActivityInGeneration(session.generation) -> null
                session.isFinished(now) -> ABSENT
                else -> null
            }
    }
}
