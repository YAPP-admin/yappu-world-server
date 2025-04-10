package co.yappuworld.attendance.infrastructure

import co.yappuworld.attendance.domain.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttendanceRepository : JpaRepository<Attendance, UUID>
