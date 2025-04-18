package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.Attendance
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttendanceRepository :
    JpaRepository<Attendance, UUID>,
    KotlinJdslJpqlExecutor {

    fun existsAttendanceByUserIdAndScheduleId(
        userId: UUID,
        scheduleId: UUID
    ): Boolean

    fun findByUserIdAndScheduleId(
        userId: UUID,
        scheduleId: UUID
    ): Attendance?
}
