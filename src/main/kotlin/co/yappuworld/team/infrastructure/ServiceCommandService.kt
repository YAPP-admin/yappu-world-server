package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.ServiceEntity
import co.yappuworld.team.infrastructure.jpa.ServiceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ServiceCommandService(
    private val serviceRepository: ServiceRepository
) {

    fun save(service: ServiceEntity): ServiceEntity = serviceRepository.save(service)

    fun delete(id: UUID) {
        serviceRepository.deleteById(id)
    }

    fun deleteAll(ids: List<UUID>) {
        require(ids.isNotEmpty()) { "최소 하나 이상의 삭제 대상이 필요합니다." }
        serviceRepository.deleteAllById(ids)
    }
}
