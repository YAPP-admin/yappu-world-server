package co.yappuworld.attendance.infrastructure

import co.yappuworld.attendance.domain.Attendance
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
    ): Attendance? = attendanceRepository.findByUserIdAndScheduleId(userId, sessionId)
}
