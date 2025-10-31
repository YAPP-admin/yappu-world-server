package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class TeamMemberCommandService(
    private val teamMemberRepository: TeamMemberRepository
) {

    fun save(teamMember: TeamMemberEntity): TeamMemberEntity = teamMemberRepository.save(teamMember)

    fun saveAll(teamMembers: List<TeamMemberEntity>) {
        require(teamMembers.isNotEmpty()) { "최소 하나의 저장 대상이 필요합니다." }
        teamMemberRepository.saveAll(teamMembers)
    }

    fun delete(id: UUID) {
        teamMemberRepository.deleteById(id)
    }

    fun deleteAll(ids: List<UUID>) {
        require(ids.isNotEmpty()) { "최소 하나 이상의 삭제 대상이 필요합니다." }
        teamMemberRepository.deleteAllById(ids)
    }
}
