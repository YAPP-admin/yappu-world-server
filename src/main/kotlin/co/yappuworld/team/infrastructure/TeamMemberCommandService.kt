package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    fun delete(teamMember: TeamMemberEntity) {
        teamMember.removeActivityUnit()
        teamMemberRepository.delete(teamMember)
    }

    fun deleteAll(teamMembers: List<TeamMemberEntity>) {
        require(teamMembers.isNotEmpty()) { "최소 하나 이상의 삭제 대상이 필요합니다." }
        teamMembers.forEach { it.removeActivityUnit() }
        teamMemberRepository.deleteAll(teamMembers)
    }

    fun deleteAll(team: TeamEntity) {
        teamMemberRepository.findByTeam(team).forEach { it.removeActivityUnit() }
        teamMemberRepository.deleteAllByTeam(team)
    }
}
