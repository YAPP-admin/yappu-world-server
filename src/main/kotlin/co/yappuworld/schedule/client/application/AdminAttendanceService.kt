package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import co.yappuworld.schedule.domain.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.user.infrastructure.UserFindService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

@Service
class AdminAttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val attendanceCommandService: AttendanceCommandService,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService
) {

    @Transactional(readOnly = true)
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

    @Transactional
    fun updateAttendance(request: AdminAttendanceUpdateRequest) {
        val attendanceBySessionAndUserId = attendanceFindService
            .findAttendances(request.getSessionAndUserIdPairs())
            .associateBy { it.scheduleId to it.userId }

        val newAttendances = mutableListOf<AttendanceEntity>()

        request.targets.forEach { target ->
            attendanceBySessionAndUserId[target.sessionId to target.userId]
                ?.apply { updateStatus(target.attendanceStatus) }
                ?: newAttendances.add(
                    AttendanceEntity(
                        status = target.attendanceStatus,
                        userId = target.userId,
                        scheduleId = target.sessionId
                    )
                )
        }

        if (newAttendances.isNotEmpty()) attendanceCommandService.saveAll(newAttendances)
    }
}
