package co.yappuworld.schedule.client.application

import co.yappuworld.external.map.MapClient
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.post.infrastructure.PostCommandService
import co.yappuworld.post.infrastructure.PostFindService
import co.yappuworld.post.infrastructure.entity.NoticeEntity
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionEligibleUsersParamRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSimpleSessionNoticePageRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionEligibleUsersResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import co.yappuworld.schedule.client.dto.response.AdminTargetableSessionNoticeResponse
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.AttendanceCommandService
import co.yappuworld.schedule.infrastructure.AttendanceFindService
import co.yappuworld.schedule.infrastructure.ScheduleCommandService
import co.yappuworld.schedule.infrastructure.SessionFindService
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.infrastructure.UserFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminScheduleService(
    private val sessionFindService: SessionFindService,
    private val scheduleCommandService: ScheduleCommandService,
    private val attendanceFindService: AttendanceFindService,
    private val attendanceCommandService: AttendanceCommandService,
    private val userFindService: UserFindService,
    private val postFindService: PostFindService,
    private val postCommandService: PostCommandService,
    private val mapClient: MapClient
) {

    @Transactional
    fun createSchedule(request: AdminSessionCreateRequest): UUID {
        val schedule = request.toDomain()
        updateAddress(schedule, request.address, request.latitude, request.longitude)

        scheduleCommandService.save(schedule)

        if (schedule is SessionEntity) {
            createAttendance(request.sessionAttendeeIds, schedule)
            linkSessionAndNotice(request.noticeIds, schedule)
        }

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
        AdminSessionDetailResponse.from(
            session = sessionFindService.findSession(id),
            attendees = attendanceFindService.findAttendees(id),
            notices = postFindService.findNoticesTargetingSession(id)
        )

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
        val session = sessionFindService.findSession(request.id)
        request.applyTo(session)
        updateAddress(session, request.address, request.latitude, request.longitude)

        handleAttendee(session, request.sessionAttendeeIds)
        adjustLinkBetweenSessionAndNotice(request.noticeIds, session)
    }

    @Transactional(readOnly = true)
    fun getSessionEligibleUsers(request: AdminSessionEligibleUsersParamRequest): AdminSessionEligibleUsersResponse =
        AdminSessionEligibleUsersResponse.from(userFindService.findActiveUsersOfGeneration(request.generation))

    @Transactional(readOnly = true)
    fun getTargetableSessionNotices(
        request: AdminSimpleSessionNoticePageRequest
    ): OffsetPageResponse<AdminTargetableSessionNoticeResponse> {
        val response = postFindService.findSessionNotices(request.toPageRequest(), request.search)
        return OffsetPageResponse.from(response) { notice ->
            AdminTargetableSessionNoticeResponse(
                id = notice.id,
                title = notice.title,
                createdAt = notice.createdAt,
                isSelectedByOtherSession = notice.targetSession != null
            )
        }
    }

    private fun createAttendance(
        attendeeIds: List<UUID>,
        session: SessionEntity
    ) {
        attendeeIds
            .takeIf { it.isNotEmpty() }
            ?.map { attendeeId -> AttendanceEntity(userId = attendeeId, session = session) }
            ?.also { attendanceCommandService.saveAll(it) }
    }

    private fun linkSessionAndNotice(
        noticeIds: List<UUID>,
        session: SessionEntity
    ) {
        val notices = postFindService.findAllByIdIn(noticeIds)

        if (notices.any { it !is NoticeEntity }) {
            throw BusinessException(ScheduleError.NOT_SESSION_NOTICE)
        }

        notices.filterIsInstance<NoticeEntity>().onEach { notice -> notice.targetSession(session) }
        postCommandService.saveAll(notices)
    }

    private fun updateAddress(
        schedule: ScheduleEntity,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ) {
        if (schedule.address == address) return

        if (address == null) {
            schedule.updateAddressAndCoordinates(null, null, null)
            return
        }

        if (latitude != null && longitude != null) {
            schedule.updateAddressAndCoordinates(address, latitude, longitude)
            return
        }

        val response = mapClient.convertAddressToCoordinates(address)
        schedule.updateAddressAndCoordinates(response.addressName, response.latitude, response.longitude)
    }

    private fun handleAttendee(
        session: SessionEntity,
        requestSessionAttendeeIds: List<UUID>
    ) {
        val attendances = attendanceFindService.findAttendances(session.id)
        val attendeeIds = attendances.map { it.userId }.toSet()

        val toCreate = requestSessionAttendeeIds
            .filterNot { attendeeId -> attendeeId in attendeeIds }
            .map { attendeeId -> AttendanceEntity(userId = attendeeId, session = session) }

        if (toCreate.isNotEmpty()) attendanceCommandService.saveAll(toCreate)

        val toDelete = attendances
            .filterNot { attendance -> attendance.userId in requestSessionAttendeeIds }

        if (toDelete.isNotEmpty()) attendanceCommandService.deleteAll(toDelete)
    }

    private fun adjustLinkBetweenSessionAndNotice(
        noticeIds: List<UUID>,
        session: SessionEntity
    ) {
        val notices = postFindService.findNoticesTargetingSession(session.id, noticeIds)
        notices.onEach { notice ->
            when (notice.id in noticeIds) {
                true -> notice.targetSession(session)
                false -> notice.detachSession()
            }
        }

        postCommandService.saveAll(notices)
    }
}
