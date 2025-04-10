package co.yappuworld.attendance.infrastructure

import co.yappuworld.attendance.domain.Attendance
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AttendanceCommandService(
    private val attendanceRepository: AttendanceRepository
) {

    fun save(attendance: Attendance) {
        attendanceRepository.save(attendance)
    }
}
