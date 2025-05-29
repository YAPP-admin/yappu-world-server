package co.yappuworld.schedule.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleCommandService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ScheduleAdminService(
    private val sessionFindService: SessionFindService,
    private val scheduleCommandService: ScheduleCommandService,
    private val attendanceFindService: AttendanceFindService,
    private val attendanceCommandService: AttendanceCommandService
) {

    @Transactional
    fun createSchedule(request: AdminSessionCreateRequest): UUID {
        val schedule = request.toDomain()
        scheduleCommandService.save(schedule)

        request.sessionAttendeeIds
            .map { attendeeId -> AttendanceEntity(userId = attendeeId, scheduleId = schedule.id) }
            .also { attendanceCommandService.saveAll(it) }

        return schedule.id
    }

    @Transactional(readOnly = true)
    fun getSessions(request: AdminSessionPageRequest): OffsetPageResponse<AdminSessionOverviewResponse> =
        when (request.generation != null) {
            true -> sessionFindService.findSessionsInGeneration(request.toPageRequest(), request.generation)
            false -> sessionFindService.findSessions(request.toPageRequest())
        }.let { OffsetPageResponse.from(it) { session -> AdminSessionOverviewResponse(session) } }

    @Transactional(readOnly = true)
    fun getSession(id: UUID): AdminSessionDetailResponse =
        AdminSessionDetailResponse(sessionFindService.findSession(id))

    /**
     * 어드민에서 요청하는 세션 삭제라, hard delete 구현
     */
    @Transactional
    fun deleteSessions(request: AdminSessionDeleteRequest) {
        val sessions = sessionFindService.findSessions(request.ids)
        if (sessions.size != request.ids.size) {
            throw BusinessException(ScheduleError.CONTAIN_IMPROPER_ID_FOR_DELETE_SESSION)
        }

        attendanceCommandService.deleteAllInSessions(request.ids)
        scheduleCommandService.deleteAll(sessions)
    }

    @Transactional
    fun updateSession(request: AdminSessionUpdateRequest) {
        sessionFindService
            .findSession(request.id)
            .apply { request.applyTo(this) }
    }
}
