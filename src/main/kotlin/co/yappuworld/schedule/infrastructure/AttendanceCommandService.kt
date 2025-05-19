package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.infrastructure.jpa.AttendanceEntity
import co.yappuworld.schedule.infrastructure.jpa.AttendanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
}
