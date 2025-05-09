package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.LatePassFindService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminAttendanceService(
    private val attendanceFindService: AttendanceFindService,
    private val attendanceCommandService: AttendanceCommandService,
    private val userFindService: UserFindService,
    private val sessionFindService: SessionFindService,
    private val generationFindService: GenerationFindService,
    private val latePassFindService: LatePassFindService
) {

    @Transactional(readOnly = true)
    fun findAttendances(now: LocalDateTime): AdminAttendancesResponse {
        val activeGeneration = generationFindService.findActiveGeneration()
        return AdminAttendancesResponse.from(
            AttendanceBook(
                generation = activeGeneration,
                users = userFindService.findUsersActiveOfGeneration(activeGeneration),
                sessions = sessionFindService.findSessionsInGeneration(activeGeneration),
                attendances = attendanceFindService.findAttendancesOfGeneration(activeGeneration),
                latePasses = latePassFindService.findLatePasses(activeGeneration),
                now = now
            )
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

    @Transactional
    fun updateSessionAttendances(request: AdminSessionAttendanceUpdateRequest) {
        val activeGeneration = generationFindService.findActiveGeneration()
        val users = userFindService.findUsersActiveOfGeneration(activeGeneration)
        val existAttendances = attendanceFindService
            .findAttendances(request.sessionId)
            .associateBy { it.userId }

        val allAttendances = users.map { user ->
            when (existAttendances.containsKey(user.userId)) {
                true -> existAttendances[user.userId]!!.apply { updateStatus(request.attendanceStatus) }
                false -> AttendanceEntity(
                    status = request.attendanceStatus,
                    userId = user.userId,
                    scheduleId = request.sessionId
                )
            }
        }

        attendanceCommandService.saveAll(allAttendances)
    }
}
