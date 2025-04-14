package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.SessionEntity
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
}
