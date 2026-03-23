package co.yappuworld.schedule.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.LocalDateRange
import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.ScheduleError
import co.yappuworld.schedule.domain.vo.SessionType
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendanceDto
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
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
    private val scheduleRepository: ScheduleRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext
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
                            .on(path(SessionEntity::getId).equal(path(AttendanceEntity::session)(SessionEntity::getId)))
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
            .findAll(CustomAttendanceDsl) {
                selectSessionWithAttendance()
                    .from(
                        entity(SessionEntity::class),
                        leftJoin(AttendanceEntity::class).on(
                            and(
                                path(SessionEntity::getId).equal(path(AttendanceEntity::session)(SessionEntity::getId)),
                                path(AttendanceEntity::userId).equal(userId)
                            )
                        )
                    ).where(path(SessionEntity::generation).equal(generation))
            }.filterNotNull()
            .onEach { it.resolveAttendanceStatusOfPastSessions(now) }

    fun findAttendancesHistories(
        generation: Int,
        userId: UUID,
        now: LocalDateTime
    ): List<SessionWithAttendanceDto> =
        scheduleRepository
            .findAll(CustomAttendanceDsl) {
                selectSessionWithAttendance()
                    .from(
                        entity(AttendanceEntity::class),
                        innerJoin(SessionEntity::class)
                            .on(path(SessionEntity::getId).equal(path(AttendanceEntity::session)(SessionEntity::getId)))
                    ).whereAnd(
                        path(AttendanceEntity::userId).equal(userId),
                        path(SessionEntity::generation).equal(generation),
                        or(
                            path(AttendanceEntity::status).notEqual(AttendanceStatus.PENDING),
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

    fun findSessions(
        pageRequest: PageRequest,
        title: String? = null,
        generation: Int? = null,
        sessionType: SessionType? = null
    ): Page<SessionEntity> {
        val result = scheduleRepository.findPage(pageRequest) {
            val predicates = buildList<Predicatable> {
                title?.takeIf { it.isNotBlank() }?.let { add(path(SessionEntity::name).like("%$it%")) }
                generation?.let { add(path(SessionEntity::generation).equal(it)) }
                sessionType?.let { add(path(SessionEntity::sessionType).equal(it)) }
            }

            select(entity(SessionEntity::class))
                .from(entity(SessionEntity::class))
                .whereAnd(*predicates.toTypedArray())
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

    fun findSessionAttendance(
        userId: UUID,
        sessionId: UUID
    ): SessionAttendance? {
        //        return scheduleRepository
        //            .singleOrNull {
        //                selectNew<SessionAttendance>(
        //                    entity(SessionEntity::class),
        //                    entity(AttendanceEntity::class)
        //                ).from(
        //                    entity(SessionEntity::class),
        //                    innerJoin(AttendanceEntity::class)
        //                        .on(path(AttendanceEntity::session)(SessionEntity::getId).eq(path(SessionEntity::getId)))
        //                ).whereAnd(
        //                    path(SessionEntity::getId).eq(sessionId),
        //                    path(AttendanceEntity::userId).eq(userId)
        //                )
        //            }

        val query = jpql {
            selectNew<SessionAttendance>(
                entity(SessionEntity::class),
                entity(AttendanceEntity::class)
            ).from(
                entity(SessionEntity::class),
                innerJoin(AttendanceEntity::class)
                    .on(path(AttendanceEntity::session)(SessionEntity::getId).eq(path(SessionEntity::getId)))
            ).whereAnd(
                path(SessionEntity::getId).eq(sessionId),
                path(AttendanceEntity::userId).eq(userId)
            )
        }
        return entityManager.createQuery(query, context).singleResult
    }

    fun findUpcomingSessionAttendance(
        userId: UUID,
        now: LocalDateTime
    ): SessionAttendance? =
        scheduleRepository
            .findAll(limit = 1) {
                selectNew<SessionAttendance>(
                    entity(SessionEntity::class),
                    entity(AttendanceEntity::class)
                ).from(
                    entity(SessionEntity::class),
                    innerJoin(AttendanceEntity::class)
                        .on(path(AttendanceEntity::session)(SessionEntity::getId).eq(path(SessionEntity::getId))),
                    innerJoin(GenerationEntity::class)
                        .on(path(GenerationEntity::value).equal(path(SessionEntity::generation)))
                ).where(
                    and(
                        path(AttendanceEntity::userId).equal(userId),
                        path(GenerationEntity::isActive).eq(true),
                        or(
                            path(SessionEntity::endDate).greaterThan(now.toLocalDate()),
                            and(
                                path(SessionEntity::endDate).equal(now.toLocalDate()),
                                path(SessionEntity::endTime).greaterThan(now.toLocalTime())
                            )
                        )
                    )
                ).orderBy(
                    path(SessionEntity::date).asc(),
                    path(SessionEntity::time).asc()
                )
            }.firstOrNull()
}
