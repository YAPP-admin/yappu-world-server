package co.yappuworld.attendance.infrastructure

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
}
