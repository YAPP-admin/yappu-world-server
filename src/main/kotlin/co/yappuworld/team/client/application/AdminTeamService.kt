package co.yappuworld.team.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.team.client.dto.request.AdminTeamPageRequest
import co.yappuworld.team.client.dto.request.AdminTeamCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamResponse
import co.yappuworld.team.client.dto.response.AdminTeamServiceResponse
import co.yappuworld.team.client.dto.response.AdminTeamMemberResponse
import co.yappuworld.team.client.dto.response.AdminTeamDetailResponse
import co.yappuworld.team.client.dto.response.UserTeamResponse
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
import co.yappuworld.user.infrastructure.UserFindService
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
    private val activityUnitFindService: ActivityUnitFindService,
    private val userFindService: UserFindService
) {

    @Transactional(readOnly = true)
    fun getTeams(request: AdminTeamPageRequest): OffsetPageResponse<AdminTeamResponse> {
        val teamsPage = teamFindService.findTeams(
            request.generation,
            request.platform,
            request.toPageRequest()
        )

        val teamIds = teamsPage.content.map { it.id }
        val services = teamServiceFindService.findServices(teamIds)
        val serviceMap = services.associateBy { it.team.id }

        return OffsetPageResponse(
            data = teamsPage.content.map { team ->
                AdminTeamResponse.from(team, serviceMap[team.id])
            },
            totalCount = teamsPage.totalElements,
            totalPages = teamsPage.totalPages,
            page = request.page,
            size = request.size
        )

    }

    @Transactional(readOnly = true)
    fun getTeam(teamId: UUID): AdminTeamDetailResponse {
        val team = teamFindService.findTeam(teamId)
        val service = getTeamService(team)
        val members = getTeamMembers(team)

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
        val team = TeamEntity(
            generation = request.generation,
            name = request.name
        ).also { teamCommandService.save(it) }

        createService(team, request)

        request.activityUnitIds?.takeIf { it.isNotEmpty() }?.let { activityUnitIds ->
            activityUnitIds.forEach { activityUnitId ->
                activityUnitFindService.findActivityUnit(activityUnitId)
                    ?: throw BusinessException(TeamError.INVALID_TEAM_MEMBERS)
            }
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

        request.activityUnitIds.takeIf { it.isNotEmpty() }?.let { activityUnitIds ->
            activityUnitIds.forEach { activityUnitId ->
                activityUnitFindService.findActivityUnit(activityUnitId)
                    ?: throw BusinessException(TeamError.INVALID_TEAM_MEMBERS)
            }
            updateTeamMembers(team, activityUnitIds)
        }
    }

    @Transactional
    fun deleteTeam(teamId: UUID) {
        val team = teamFindService.findTeam(teamId)

        val members = teamMemberFindService.findMembers(team)
        if (members.isNotEmpty()) {
            val memberIds = members.map { it.id }
            try {
                teamMemberCommandService.deleteAll(memberIds)
            } catch (e: IllegalArgumentException) {
                throw BusinessException(TeamError.INVALID_DELETE_REQUEST)
            }
        }

        val service = teamServiceFindService.findServiceOrNull(team)
        service?.let {
            try {
                teamServiceCommandService.delete(it.id)
            } catch (e: IllegalArgumentException) {
                throw BusinessException(TeamError.INVALID_DELETE_REQUEST)
            }
        }

        teamCommandService.delete(teamId)
    }

    @Transactional(readOnly = true)
    fun getTeamByActivityUnitId(activityUnitId: UUID): UserTeamResponse? {
        val teamMember = teamMemberFindService.findMemberOrNull(activityUnitId)
            ?: return null

        return UserTeamResponse(
            id = teamMember.team.id,
            name = teamMember.team.name
        )
    }

    @Transactional(readOnly = true)
    fun getTeamsByGeneration(generation: Int): List<UserTeamResponse> =
        teamFindService.findTeams(generation).map { team ->
            UserTeamResponse(
                id = team.id,
                name = team.name
            )
        }

    @Transactional
    fun assignMemberToTeam(
        activityUnitId: UUID,
        teamId: UUID?
    ) {
        teamMemberFindService.findMemberOrNull(activityUnitId)?.let { existingMember ->
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
            TeamMemberEntity(team = team, activityUnitId = activityUnitId)
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
        if (existingMembers.isNotEmpty()) {
            val memberIds = existingMembers.map { it.id }
            try {
                teamMemberCommandService.deleteAll(memberIds)
            } catch (e: IllegalArgumentException) {
                throw BusinessException(TeamError.INVALID_DELETE_REQUEST)
            }
        }
        createTeamMembers(team, activityUnitIds)
    }

    private fun toServiceResponse(teamServiceEntity: TeamServiceEntity): AdminTeamServiceResponse =
        AdminTeamServiceResponse(
            id = teamServiceEntity.id,
            name = teamServiceEntity.name,
            hasApp = teamServiceEntity.hasApp,
            hasWeb = teamServiceEntity.hasWeb,
            googlePlayLink = teamServiceEntity.serviceLinks?.googlePlay,
            appStoreLink = teamServiceEntity.serviceLinks?.appStore,
            webLink = teamServiceEntity.serviceLinks?.web
        )

    private fun getTeamService(team: TeamEntity): AdminTeamServiceResponse? =
        teamServiceFindService.findServiceOrNull(team)?.let { toServiceResponse(it) }

    private fun getTeamMembers(team: TeamEntity): List<AdminTeamMemberResponse> {
        val teamMembers = teamMemberFindService.findMembers(team)

        val activityUnits = teamMembers.mapNotNull { member ->
            activityUnitFindService.findActivityUnit(
                member.activityUnitId
            )
        }

        val users = userFindService.findAllByIdIn(activityUnits.map { it.userId }).associate { it.id to it }
        val activityUnitMap = activityUnits.associateBy { it.id }

        return teamMembers.mapNotNull { member ->
            val unit = activityUnitMap[member.activityUnitId]
            val user = unit?.let { users[it.userId] }
            user?.let {
                AdminTeamMemberResponse(
                    activityUnitId = member.activityUnitId,
                    name = it.name,
                    position = unit.position.name
                )
            }
        }
    }
}
