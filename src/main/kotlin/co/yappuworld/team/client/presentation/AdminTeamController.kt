package co.yappuworld.team.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.team.client.dto.request.AdminTeamCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamPageRequest
import co.yappuworld.team.client.dto.request.AdminTeamUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamResponse
import co.yappuworld.team.client.dto.response.AdminTeamDetailResponse
import co.yappuworld.team.client.application.AdminTeamService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class AdminTeamController(
    private val adminTeamService: AdminTeamService
) : AdminTeamApi {

    override fun getTeams(
        request: AdminTeamPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminTeamResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(adminTeamService.getTeams(request))
        )

    override fun getTeam(teamId: UUID): ResponseEntity<SuccessResponse<AdminTeamDetailResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminTeamService.getTeam(teamId))
        )

    override fun createTeam(request: AdminTeamCreateRequest): ResponseEntity<Unit> {
        val teamId = adminTeamService.createTeam(request)
        return ResponseEntity.created(URI("/v1/admin/teams/$teamId")).build()
    }

    override fun updateTeam(request: AdminTeamUpdateRequest): ResponseEntity<Unit> {
        adminTeamService.updateTeam(request)
        return ResponseEntity.noContent().build()
    }

    override fun deleteTeam(teamId: UUID): ResponseEntity<Unit> {
        adminTeamService.deleteTeam(teamId)
        return ResponseEntity.noContent().build()
    }
}
