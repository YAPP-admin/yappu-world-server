package co.yappuworld.schedule.application

import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.schedule.application.dto.response.SessionsAppResponseDto
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ScheduleService(
    private val generationRepository: GenerationRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository
) {

    @Transactional(readOnly = true)
    fun getCurrentGenerationSessions(now: LocalDate): SessionsAppResponseDto {
        val currentGeneration = generationRepository.getGenerationOrNullByIsActiveIsTrue()
            ?: return SessionsAppResponseDto.from(emptyList(), now)
        return SessionsAppResponseDto.from(
            sessions = scheduleJpaRepository.findSessionEntitiesByGeneration(currentGeneration.value),
            now = now
        )
    }
}
