package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.Attendance
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

    fun findCurrentGenerationSessions(generation: Int): List<SessionEntity> =
        scheduleRepository
            .findAll {
                select(entity(SessionEntity::class))
                    .from(entity(SessionEntity::class))
                    .where(path(SessionEntity::generation).equal(generation))
            }.filterNotNull()

    fun findSessionsWithAttendanceStatus(
        generation: Int,
        userId: UUID
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
                    path(Attendance::createdAt),
                    path(Attendance::status)
                ).from(
                    entity(SessionEntity::class),
                    leftJoin(Attendance::class).on(
                        and(
                            path(SessionEntity::generation).equal(generation),
                            path(SessionEntity::getId).equal(path(Attendance::scheduleId)),
                            path(Attendance::userId).equal(userId)
                        )
                    )
                )
            }.filterNotNull()

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
                    path(Attendance::createdAt),
                    path(Attendance::status)
                ).from(
                    entity(SessionEntity::class),
                    leftJoin(Attendance::class).on(
                        and(
                            path(SessionEntity::generation).equal(generation),
                            path(SessionEntity::getId).equal(path(Attendance::scheduleId)),
                            path(Attendance::userId).equal(userId)
                        )
                    )
                ).where(
                    or(
                        path(SessionEntity::date).lessThan(now.toLocalDate()),
                        and(
                            path(SessionEntity::date).equal(now.toLocalDate()),
                            path(SessionEntity::time).lessThan(now.toLocalTime())
                        )
                    )
                )
            }.filterNotNull()
}
