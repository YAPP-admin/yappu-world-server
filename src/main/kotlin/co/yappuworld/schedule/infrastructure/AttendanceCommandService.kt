package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class AttendanceCommandService(
    private val attendanceRepository: AttendanceRepository
) {

    fun checkIn(sessionAttendance: SessionAttendance) {
        attendanceRepository.save(sessionAttendance.getNewAttendance())
    }

    fun saveAll(attendances: List<AttendanceEntity>) {
        require(attendances.isNotEmpty()) { "저장을 위한 출석 데이터는 적어도 하나 이상이어야 합니다." }
        attendanceRepository.saveAll(attendances)
    }

    fun deleteAll(attendances: List<AttendanceEntity>) {
        require(attendances.isNotEmpty()) { "삭제를 위한 출석 데이터는 적어도 하나 이상이어야 합니다." }
        attendanceRepository.deleteAll(attendances)
    }

    fun deleteAllInSessions(sessionIds: List<UUID>) {
        require(sessionIds.isNotEmpty()) { "삭제를 위한 세션 ID는 적어도 하나 이상이어야 합니다." }
        attendanceRepository.delete {
            deleteFrom(entity(AttendanceEntity::class))
                .where(path(AttendanceEntity::session)(SessionEntity::getId).`in`(sessionIds))
        }
    }
}
