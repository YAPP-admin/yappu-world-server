package co.yappuworld.schedule.client.application

import co.yappuworld.operation.infrastructure.GenerationJpaRepository
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.infrastructure.ScheduleJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ScheduleService(
    private val generationRepository: GenerationJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository
) {

    @Transactional(readOnly = true)
    fun getCurrentGenerationSessions(now: LocalDate): ActiveGenerationSessionsResponse {
        val currentGeneration = generationRepository.getGenerationOrNullByIsActiveIsTrue()
            ?: return ActiveGenerationSessionsResponse.from(emptyList(), now)
        return ActiveGenerationSessionsResponse.from(
            sessions = scheduleJpaRepository.findAllSessionEntityByGeneration(currentGeneration.value),
            now = now
        )
    }

    fun getSchedules(
        request: SchedulePageRequest,
        now: LocalDateTime
    ): SchedulePageResponse {
        val schedules = scheduleJpaRepository.findScheduleEntitiesByDateBetween(request.from, request.to)
        return SchedulePageResponse.from(schedules, request, now)
    }
}
