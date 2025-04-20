package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.AttendanceEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AttendanceFindService(
    private val attendanceRepository: AttendanceRepository
) {

    fun hasAlreadyCheckedIn(
        userId: UUID,
        sessionId: UUID
    ): Boolean = attendanceRepository.existsAttendanceByUserIdAndScheduleId(userId, sessionId)

    fun findSessionAttendance(
        userId: UUID,
        sessionId: UUID
    ): AttendanceEntity? = attendanceRepository.findByUserIdAndScheduleId(userId, sessionId)

    fun findAttendancesBySchedules(
        userId: UUID,
        scheduleIds: List<UUID>
    ): List<AttendanceEntity> =
        attendanceRepository
            .findAll {
                select(entity(AttendanceEntity::class))
                    .from(entity(AttendanceEntity::class))
                    .where(
                        and(
                            path(AttendanceEntity::userId).equal(userId),
                            path(AttendanceEntity::scheduleId).`in`(scheduleIds)
                        )
                    )
            }.filterNotNull()
}
