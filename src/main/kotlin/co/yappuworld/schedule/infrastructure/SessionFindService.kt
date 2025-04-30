package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.AttendanceEntity
import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendance
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

    fun findSession(id: UUID) = scheduleRepository.findByIdOrNull(id)

    fun findUpcomingSession(
        activeGeneration: Int,
        now: LocalDateTime
    ): SessionEntity? =
        scheduleRepository
            .findAll(limit = 1) {
                select(entity(SessionEntity::class))
                    .from(entity(SessionEntity::class))
                    .where(
                        and(
                            path(SessionEntity::generation).equal(activeGeneration),
                            path(SessionEntity::date).greaterThanOrEqualTo(now.toLocalDate())
                        )
                    ).orderBy(path(SessionEntity::date).asc())
            }.singleOrNull()

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
            .apply { forEach { it.resolveAttendanceStatusOfPastSessions(now) } }
}
