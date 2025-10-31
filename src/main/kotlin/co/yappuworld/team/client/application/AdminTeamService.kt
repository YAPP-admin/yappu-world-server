package co.yappuworld.team.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.team.client.dto.request.AdminTeamPageRequest
import co.yappuworld.team.client.dto.request.AdminTeamCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamUpdateRequest
import co.yappuworld.team.client.dto.request.AdminServiceCreateRequest
import co.yappuworld.team.client.dto.request.AdminServiceUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamResponse
import co.yappuworld.team.client.dto.response.AdminTeamDetailResponse
import co.yappuworld.team.client.dto.response.AdminServiceResponse
import co.yappuworld.team.client.dto.response.AdminServiceLinksResponse
import co.yappuworld.team.client.dto.response.AdminTeamMemberResponse
import co.yappuworld.team.client.dto.response.UserTeamResponse
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamCommandService
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.TeamMemberCommandService
import co.yappuworld.team.infrastructure.ServiceCommandService
import co.yappuworld.team.infrastructure.entity.ServiceEntity
import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminTeamService(
    private val teamFindService: TeamFindService,
    private val teamMemberFindService: TeamMemberFindService,
    private val teamCommandService: TeamCommandService,
    private val serviceCommandService: ServiceCommandService,
    private val teamMemberCommandService: TeamMemberCommandService
) {

    @Transactional(readOnly = true)
    fun getTeams(request: AdminTeamPageRequest): OffsetPageResponse<AdminTeamResponse> =
        teamFindService
            .findTeams(
                request.generation,
                request.platform,
                request.toPageRequest()
            ).let {
                OffsetPageResponse(
                    data = it.content.map { team -> AdminTeamResponse(team) },
                    totalCount = it.totalElements,
                    totalPages = it.totalPages,
                    page = request.page,
                    size = request.size
                )
            }

    @Transactional(readOnly = true)
    fun getTeam(teamId: UUID): AdminTeamDetailResponse {
        val team = teamFindService.findTeam(teamId)

        val service = team.service?.let { toServiceResponse(it) }
        val members = teamMemberFindService.findByTeam(team).map { teamMember ->
            AdminTeamMemberResponse(activityUnitId = teamMember.activityUnitId)
        }

        return AdminTeamDetailResponse(
            id = team.id,
            generation = team.generation,
            name = team.name,
            service = service,
            members = members
        )
    }

    @Transactional
    fun createTeam(request: AdminTeamCreateRequest): UUID {
        val service = request.service?.let { createService(it) }

        val team = TeamEntity(
            generation = request.generation,
            name = request.name,
            service = service
        ).also { teamCommandService.save(it) }

        createTeamMembers(team, request.activityUnitIds)

        return team.id
    }

    private fun createService(request: AdminServiceCreateRequest): ServiceEntity =
        ServiceEntity(
            name = request.name,
            hasApp = request.hasApp,
            hasWeb = request.hasWeb,
            serviceLinks = request.serviceLinks?.let {
                ServiceLinks(
                    googleStore = it.googleStore,
                    appStore = it.appStore,
                    web = it.web
                )
            }
        ).also { serviceCommandService.save(it) }

    private fun createTeamMembers(
        team: TeamEntity,
        activityUnitIds: List<UUID>
    ) {
        val teamMembers = activityUnitIds.map { activityUnitId ->
            TeamMemberEntity(team = team, activityUnitId = activityUnitId)
        }
        teamMemberCommandService.saveAll(teamMembers)
    }

    @Transactional
    fun updateTeam(request: AdminTeamUpdateRequest) {
        val team = teamFindService.findTeam(request.id)

        val service = request.service?.let { updateService(team, it) }

        team.update(
            generation = request.generation,
            name = request.name,
            service = service
        )

        updateTeamMembers(team, request.activityUnitIds)
    }

    private fun updateService(
        team: TeamEntity,
        request: AdminServiceUpdateRequest
    ): ServiceEntity =
        team.service?.apply {
            update(
                name = request.name,
                hasApp = request.hasApp,
                hasWeb = request.hasWeb,
                serviceLinks = request.serviceLinks?.let {
                    ServiceLinks(
                        googleStore = it.googleStore,
                        appStore = it.appStore,
                        web = it.web
                    )
                }
            )
        } ?: throw BusinessException(TeamError.SERVICE_NOT_FOUND)

    private fun updateTeamMembers(
        team: TeamEntity,
        activityUnitIds: List<UUID>
    ) {
        val existingMemberIds = teamMemberFindService.findActivityUnitIdsByTeam(team)
        if (existingMemberIds.isNotEmpty()) {
            teamMemberCommandService.deleteAll(existingMemberIds)
        }
        createTeamMembers(team, activityUnitIds)
    }

    private fun toServiceResponse(serviceEntity: ServiceEntity): AdminServiceResponse =
        AdminServiceResponse(
            id = serviceEntity.id,
            name = serviceEntity.name,
            hasApp = serviceEntity.hasApp,
            hasWeb = serviceEntity.hasWeb,
            serviceLinks = serviceEntity.serviceLinks?.let { links ->
                AdminServiceLinksResponse(
                    googleStore = links.googleStore,
                    appStore = links.appStore,
                    web = links.web
                )
            }
        )

    @Transactional
    fun deleteTeam(teamId: UUID) {
        val team = teamFindService.findTeam(teamId)

        val memberIds = teamMemberFindService.findActivityUnitIdsByTeam(team)

        if (memberIds.isNotEmpty()) {
            teamMemberCommandService.deleteAll(memberIds)
        }

        team.service?.let { serviceCommandService.delete(it.id) }

        teamCommandService.delete(teamId)
    }

    @Transactional(readOnly = true)
    fun getTeamByActivityUnitId(activityUnitId: UUID): UserTeamResponse? {
        val teamMember = teamMemberFindService.findByActivityUnitIdOrNull(activityUnitId)
            ?: return null

        return UserTeamResponse(
            id = teamMember.team.id,
            name = teamMember.team.name
        )
    }

    @Transactional(readOnly = true)
    fun getTeamsByGeneration(generation: Int): List<UserTeamResponse> =
        teamFindService.findByGeneration(generation).map { team ->
            UserTeamResponse(
                id = team.id,
                name = team.name
            )
        }

    @Transactional
    fun assignTeamToActivityUnit(
        activityUnitId: UUID,
        teamId: UUID?
    ) {
        teamMemberFindService.findByActivityUnitIdOrNull(activityUnitId)?.let { existingMember ->
            teamMemberCommandService.delete(existingMember.id)
        }

        if (teamId != null) {
            val team = teamFindService.findTeam(teamId)
            val teamMember = TeamMemberEntity(
                team = team,
                activityUnitId = activityUnitId
            )
            teamMemberCommandService.save(teamMember)
        }
    }
}
