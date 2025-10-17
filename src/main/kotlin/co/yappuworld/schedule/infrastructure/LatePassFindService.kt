package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class LatePassFindService(
    private val latePassRepository: LatePassRepository
) {

    fun countLatePasses(
        generation: Int,
        userId: UUID
    ): Int = latePassRepository.countAllByGenerationAndUserId(generation, userId)

    fun findLatePasses(generation: Int): List<LatePassEntity> = latePassRepository.findAllByGeneration(generation)

    fun findLatePasses(
        generation: Int,
        userIds: List<UUID>
    ): List<LatePassEntity> = latePassRepository.findAllByGenerationAndUserIdIn(generation, userIds)
}
