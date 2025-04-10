package co.yappuworld.schedule.infrastructure

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SessionFindService(
    private val scheduleRepository: ScheduleRepository
) {

    fun findSession(id: UUID) = scheduleRepository.findByIdOrNull(id)
}
