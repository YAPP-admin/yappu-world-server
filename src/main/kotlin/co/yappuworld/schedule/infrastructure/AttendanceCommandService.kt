package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.AttendanceEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AttendanceCommandService(
    private val attendanceRepository: AttendanceRepository
) {

    fun save(attendance: AttendanceEntity) {
        attendanceRepository.save(attendance)
    }
}
