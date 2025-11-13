package co.yappuworld.team.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.team.client.dto.request.AdminTeamPageRequest
import co.yappuworld.team.client.dto.request.AdminTeamCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamResponse
import co.yappuworld.team.client.dto.response.AdminTeamDetailResponse
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamCommandService
import co.yappuworld.team.infrastructure.TeamServiceCommandService
import co.yappuworld.team.infrastructure.TeamMemberCommandService
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.user.infrastructure.ActivityUnitFindService
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminTeamService(
    private val teamFindService: TeamFindService,
    private val teamMemberFindService: TeamMemberFindService,
    private val teamCommandService: TeamCommandService,
    private val teamServiceFindService: TeamServiceFindService,
    private val teamServiceCommandService: TeamServiceCommandService,
    private val teamMemberCommandService: TeamMemberCommandService,
    private val activityUnitFindService: ActivityUnitFindService
) {

    @Transactional(readOnly = true)
    fun getTeams(request: AdminTeamPageRequest): OffsetPageResponse<AdminTeamResponse> {
        val teamsPage = teamFindService.findTeams(
            request.generation,
            request.platform,
            request.toPageRequest()
        )

        return OffsetPageResponse(
            data = teamsPage.content.map { dto -> AdminTeamResponse.from(dto) },
            totalCount = teamsPage.totalElements,
            totalPages = teamsPage.totalPages,
            page = request.page,
            size = request.size
        )
    }

    @Transactional(readOnly = true)
    fun getTeam(teamId: UUID): AdminTeamDetailResponse {
        val teamWithService = teamFindService.findTeamWithService(teamId)
        val members = teamMemberFindService.findTeamMembersDetail(teamId).filterNotNull()

        return AdminTeamDetailResponse.of(teamWithService, members)
    }

    @Transactional
    fun createTeam(request: AdminTeamCreateRequest): UUID {
        val team = request.toTeam().also { teamCommandService.save(it) }

        createService(team, request)

        request.activityUnitIds?.takeIf { it.isNotEmpty() }?.let { activityUnitIds ->
            validateActivityUnits(team, activityUnitIds)
            createTeamMembers(team, activityUnitIds)
        }

        return team.id
    }

    @Transactional
    fun updateTeam(request: AdminTeamUpdateRequest) {
        val team = teamFindService.findTeam(request.id)

        team.update(
            generation = request.generation,
            name = request.name
        )

        updateService(team, request)

        request.activityUnitIds?.let { activityUnitIds ->
            when (activityUnitIds.isEmpty()) {
                true -> deleteTeamMembers(team)
                false -> {
                    validateActivityUnits(team, activityUnitIds)
                    updateTeamMembers(team, activityUnitIds)
                }
            }
        }
    }

    @Transactional
    fun deleteTeam(teamId: UUID) {
        val team = teamFindService.findTeam(teamId)

        deleteTeamMembers(team)
        deleteTeamService(team)

        teamCommandService.delete(teamId)
    }

    @Transactional
    fun assignMemberToTeam(
        activityUnit: ActivityUnitEntity,
        teamId: UUID?
    ) {
        teamMemberFindService.findMemberOrNull(activityUnit)?.let { existingMember ->
            teamMemberCommandService.delete(existingMember)
        }

        if (teamId != null) {
            val team = teamFindService.findTeam(teamId)
            val teamMember = TeamMemberEntity(
                team = team,
                activityUnit = activityUnit
            )
            teamMemberCommandService.save(teamMember)
        }
    }

    private fun validateActivityUnits(
        team: TeamEntity,
        activityUnitIds: List<UUID>
    ) {
        activityUnitIds.forEach { activityUnitId ->
            val activityUnit = activityUnitFindService.findActivityUnit(activityUnitId)
                ?: throw BusinessException(TeamError.INVALID_ACTIVITY_UNIT)

            if (activityUnit.generation != team.generation) {
                throw BusinessException(TeamError.INVALID_ACTIVITY_UNIT_GENERATION)
            }
        }
    }

    private fun createService(
        team: TeamEntity,
        request: AdminTeamCreateRequest
    ) {
        TeamServiceEntity(
            team = team,
            name = request.serviceName,
            hasApp = request.hasApp,
            hasWeb = request.hasWeb,
            serviceLinks = ServiceLinks(
                googlePlay = request.googlePlayLink,
                appStore = request.appStoreLink,
                web = request.webLink
            )
        ).also { teamServiceCommandService.save(it) }
    }

    private fun createTeamMembers(
        team: TeamEntity,
        activityUnitIds: List<UUID>
    ) {
        val teamMembers = activityUnitIds.map { activityUnitId ->
            val activityUnit = activityUnitFindService.findActivityUnit(activityUnitId)
                ?: throw BusinessException(TeamError.INVALID_ACTIVITY_UNIT)

            TeamMemberEntity(team = team, activityUnit = activityUnit)
        }

        try {
            teamMemberCommandService.saveAll(teamMembers)
        } catch (e: IllegalArgumentException) {
            throw BusinessException(TeamError.INVALID_TEAM_MEMBERS)
        }
    }

    private fun updateService(
        team: TeamEntity,
        request: AdminTeamUpdateRequest
    ) {
        val existingService = teamServiceFindService.findServiceOrNull(team)

        if (existingService != null) {
            existingService.update(
                name = request.serviceName,
                hasApp = request.hasApp,
                hasWeb = request.hasWeb,
                serviceLinks = ServiceLinks(
                    googlePlay = request.googlePlayLink,
                    appStore = request.appStoreLink,
                    web = request.webLink
                )
            )
        } else {
            TeamServiceEntity(
                team = team,
                name = request.serviceName,
                hasApp = request.hasApp,
                hasWeb = request.hasWeb,
                serviceLinks = ServiceLinks(
                    googlePlay = request.googlePlayLink,
                    appStore = request.appStoreLink,
                    web = request.webLink
                )
            ).also { teamServiceCommandService.save(it) }
        }
    }

    private fun updateTeamMembers(
        team: TeamEntity,
        activityUnitIds: List<UUID>
    ) {
        val existingMembers = teamMemberFindService.findMembers(team)
        val existingActivityUnitIds = existingMembers.map { it.activityUnit.id }

        activityUnitIds
            .filterNot { it in existingActivityUnitIds }
            .takeIf { it.isNotEmpty() }
            ?.let { createTeamMembers(team, it) }

        existingMembers
            .filterNot { it.activityUnit.id in activityUnitIds }
            .takeIf { it.isNotEmpty() }
            ?.let { teamMemberCommandService.deleteAll(it) }
    }

    private fun deleteTeamMembers(team: TeamEntity) {
        try {
            teamMemberCommandService.deleteAll(team)
        } catch (e: IllegalArgumentException) {
            throw BusinessException(TeamError.INVALID_DELETE_REQUEST)
        }
    }

    private fun deleteTeamService(team: TeamEntity) {
        teamServiceFindService.findServiceOrNull(team)?.let { service ->
            teamServiceCommandService.delete(service)
        }
    }
}
