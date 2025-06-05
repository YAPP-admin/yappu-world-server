package co.yappuworld.schedule.domain.vo

import co.yappuworld.schedule.domain.vo.AttendanceStatus.EXCUSED_ABSENCE
import co.yappuworld.schedule.domain.vo.AttendanceStatus.ON_TIME
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.model.UserWithActivityUnits
import java.time.LocalDateTime

/**
 * @property PENDING 유저의 출석 액션이 이뤄지지 않은 상태 - 유저에게는 결석으로 노출될 수 있음
 * @property ON_TIME 제 시간에 정상적으로 출석
 * @property EXCUSED_ABSENCE 출석으로 인정되는 결석
 */
enum class AttendanceStatus(
    val label: String
) {
    PENDING("미출석"),
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
                session.isFinished(now) -> AttendanceStatus.ABSENT
                else -> null
            }
    }
}
