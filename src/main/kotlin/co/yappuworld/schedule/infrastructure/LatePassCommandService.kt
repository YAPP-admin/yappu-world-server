package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.entity.LatePassEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LatePassCommandService(
    private val latePassRepository: LatePassRepository
) {

    fun saveAll(latePassEntities: List<LatePassEntity>) {
        latePassRepository.saveAll(latePassEntities)
    }
}
