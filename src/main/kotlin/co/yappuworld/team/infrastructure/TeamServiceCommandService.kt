package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TeamServiceCommandService(
    private val serviceRepository: TeamServiceRepository
) {

    fun save(service: TeamServiceEntity): TeamServiceEntity = serviceRepository.save(service)

    fun delete(service: TeamServiceEntity) {
        serviceRepository.delete(service)
    }

    fun deleteAll(services: List<TeamServiceEntity>) {
        require(services.isNotEmpty()) { "최소 하나 이상의 삭제 대상이 필요합니다." }
        serviceRepository.deleteAll(services)
    }
}
