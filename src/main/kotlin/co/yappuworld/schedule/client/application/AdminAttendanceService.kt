package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AdminAttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService
) {

    fun findAttendances(now: LocalDateTime): AdminAttendancesResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        val sessions = sessionFindService.findSessionsInGeneration(activeGeneration)
        val users = userFindService.findUsersActiveOfGeneration(activeGeneration)
        val attendances = attendanceFindService.findAttendancesOfGeneration(activeGeneration)

        return AdminAttendancesResponse.from(
            sessions = sessions,
            users = users,
            attendances = attendances,
            now = now
        )
    }
}
