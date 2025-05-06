package co.yappuworld.schedule.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendance
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SessionFindService(
    private val scheduleRepository: ScheduleRepository
) {

    fun findSession(id: UUID): SessionEntity =
        scheduleRepository.findByIdOrNull(id) as? SessionEntity
            ?: throw BusinessException(ScheduleError.NOT_FOUND_SESSION)

    fun findUpcomingSession(
        activeGeneration: Int,
        now: LocalDateTime
    ): SessionEntity =
        scheduleRepository
            .findAll(limit = 1) {
                select(entity(SessionEntity::class))
                    .from(entity(SessionEntity::class))
                    .where(
                        and(
                            path(SessionEntity::generation).equal(activeGeneration),
                            or(
                                path(SessionEntity::date).greaterThan(now.toLocalDate()),
                                and(
                                    path(SessionEntity::date).equal(now.toLocalDate()),
                                    path(SessionEntity::endTime).greaterThan(now.toLocalTime())
                                )
                            )
                        )
                    ).orderBy(path(SessionEntity::date).asc())
            }.singleOrNull()
            ?: throw BusinessException(ScheduleError.NO_UPCOMING_SESSION)

    fun findSessionsInGeneration(generation: Int): List<SessionEntity> =
        scheduleRepository
            .findAll {
                select(entity(SessionEntity::class))
                    .from(entity(SessionEntity::class))
                    .where(path(SessionEntity::generation).equal(generation))
            }.filterNotNull()

    fun findSessionsWithAttendanceStatus(
        generation: Int,
        userId: UUID,
        now: LocalDateTime
    ): List<SessionWithAttendance> =
        scheduleRepository
            .findAll {
                selectNew<SessionWithAttendance>(
                    path(SessionEntity::getId),
                    path(SessionEntity::name),
                    path(SessionEntity::description),
                    path(SessionEntity::place),
                    path(SessionEntity::date),
                    path(SessionEntity::endDate),
                    path(SessionEntity::time),
                    path(SessionEntity::endTime),
                    path(SessionEntity::generation),
                    path(SessionEntity::sessionType),
                    path(AttendanceEntity::createdAt),
                    path(AttendanceEntity::status)
                ).from(
                    entity(SessionEntity::class),
                    leftJoin(AttendanceEntity::class).on(
                        and(
                            path(SessionEntity::getId).equal(path(AttendanceEntity::scheduleId)),
                            path(AttendanceEntity::userId).equal(userId)
                        )
                    )
                ).where(path(SessionEntity::generation).equal(generation))
            }.filterNotNull()
            .apply { forEach { it.resolveAttendanceStatusOfPastSessions(now) } }

    fun findAttendancesHistory(
        generation: Int,
        userId: UUID,
        now: LocalDateTime
    ): List<SessionWithAttendance> =
        scheduleRepository
            .findAll {
                selectNew<SessionWithAttendance>(
                    path(SessionEntity::getId),
                    path(SessionEntity::name),
                    path(SessionEntity::description),
                    path(SessionEntity::place),
                    path(SessionEntity::date),
                    path(SessionEntity::endDate),
                    path(SessionEntity::time),
                    path(SessionEntity::endTime),
                    path(SessionEntity::generation),
                    path(SessionEntity::sessionType),
                    path(AttendanceEntity::createdAt),
                    path(AttendanceEntity::status)
                ).from(
                    entity(SessionEntity::class),
                    leftJoin(AttendanceEntity::class).on(
                        and(
                            path(SessionEntity::getId).equal(path(AttendanceEntity::scheduleId)),
                            path(AttendanceEntity::userId).equal(userId)
                        )
                    )
                ).where(
                    and(
                        path(SessionEntity::generation).equal(generation),
                        or(
                            path(SessionEntity::endDate).lessThan(now.toLocalDate()),
                            and(
                                path(SessionEntity::endDate).equal(now.toLocalDate()),
                                path(SessionEntity::endTime).lessThan(now.toLocalTime())
                            )
                        )
                    )
                )
            }.filterNotNull()
            .onEach { it.resolveAttendanceStatusOfPastSessions(now) }

    fun findSessions(ids: List<UUID>): List<SessionEntity> = scheduleRepository.findAllByIdIn(ids)

    fun findSessions(pageRequest: PageRequest): Page<SessionEntity> {
        val result = scheduleRepository.findPage(pageRequest) {
            select(entity(SessionEntity::class))
                .from(entity(SessionEntity::class))
        }

        return PageImpl(result.content.filterNotNull(), result.pageable, result.totalElements)
    }

    fun findSessionsInGeneration(
        pageRequest: PageRequest,
        generation: Int
    ): Page<SessionEntity> {
        val result = scheduleRepository.findPage(pageRequest) {
            select(entity(SessionEntity::class))
                .from(entity(SessionEntity::class))
                .where(path(SessionEntity::generation).equal(generation))
        }

        return PageImpl(result.content.filterNotNull(), result.pageable, result.totalElements)
    }
}
