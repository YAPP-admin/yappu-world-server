package co.yappuworld.schedule.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.LocalDateRange
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendanceDto
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
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
        userId: UUID,
        activeGeneration: Int,
        now: LocalDateTime
    ): SessionEntity =
        scheduleRepository
            .findAll(limit = 1) {
                select(entity(SessionEntity::class))
                    .from(
                        entity(SessionEntity::class),
                        innerJoin(AttendanceEntity::class)
                            .on(path(SessionEntity::getId).equal(path(AttendanceEntity::scheduleId)))
                    ).where(
                        and(
                            path(SessionEntity::generation).equal(activeGeneration),
                            path(AttendanceEntity::userId).equal(userId),
                            or(
                                path(SessionEntity::endDate).greaterThan(now.toLocalDate()),
                                and(
                                    path(SessionEntity::endDate).equal(now.toLocalDate()),
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
    ): List<SessionWithAttendanceDto> =
        scheduleRepository
            .findAll {
                selectNew<SessionWithAttendanceDto>(
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
    ): List<SessionWithAttendanceDto> =
        scheduleRepository
            .findAll {
                selectNew<SessionWithAttendanceDto>(
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
                ).whereAnd(
                    path(SessionEntity::generation).equal(generation),
                    or(
                        path(SessionEntity::endDate).lessThan(now.toLocalDate()),
                        and(
                            path(SessionEntity::endDate).equal(now.toLocalDate()),
                            path(SessionEntity::endTime).lessThan(now.toLocalTime())
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
                .orderBy(
                    path(SessionEntity::date).desc(),
                    path(SessionEntity::time).desc(),
                    path(SessionEntity::endDate).desc(),
                    path(SessionEntity::endTime).desc()
                )
        }

        return PageImpl(result.content.filterNotNull(), result.pageable, result.totalElements)
    }

    fun findSessions(
        generation: Int? = null,
        range: LocalDateRange? = null
    ): List<SessionEntity> =
        scheduleRepository
            .findAll(CustomSessionDsl) {
                val predicates = mutableListOf<Predicatable>()
                generation?.let { predicates.add(path(SessionEntity::generation).equal(it)) }
                range?.let { predicates.add(path(SessionEntity::date).between(range.start, range.last)) }

                select(entity(SessionEntity::class))
                    .from(entity(SessionEntity::class))
                    .whereAnd(*predicates.toTypedArray())
                    .orderBy(*sessionSorting().toTypedArray())
            }.filterNotNull()

    fun findSessionsInGeneration(
        pageRequest: PageRequest,
        generation: Int
    ): Page<SessionEntity> {
        val result = scheduleRepository.findPage(pageRequest) {
            select(entity(SessionEntity::class))
                .from(entity(SessionEntity::class))
                .where(path(SessionEntity::generation).equal(generation))
                .orderBy(
                    path(SessionEntity::date).desc(),
                    path(SessionEntity::time).desc(),
                    path(SessionEntity::endDate).desc(),
                    path(SessionEntity::endTime).desc()
                )
        }

        return PageImpl(result.content.filterNotNull(), result.pageable, result.totalElements)
    }
}
