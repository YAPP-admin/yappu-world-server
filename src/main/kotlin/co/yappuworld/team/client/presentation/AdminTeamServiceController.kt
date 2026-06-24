package co.yappuworld.team.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.team.client.application.AdminTeamServiceManageService
import co.yappuworld.team.client.dto.request.AdminTeamServiceCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamServicePageRequest
import co.yappuworld.team.client.dto.request.AdminTeamServiceUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamServiceDetailResponse
import co.yappuworld.team.client.dto.response.AdminTeamServiceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.UUID

@RestController
class AdminTeamServiceController(
    private val adminTeamServiceManageService: AdminTeamServiceManageService
) : AdminTeamServiceApi {

    override fun getTeamServices(
        request: AdminTeamServicePageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminTeamServiceResponse>>> =
        ResponseEntity.ok(SuccessResponse(adminTeamServiceManageService.getTeamServices(request)))

    override fun getTeamService(serviceId: UUID): ResponseEntity<SuccessResponse<AdminTeamServiceDetailResponse>> =
        ResponseEntity.ok(SuccessResponse(adminTeamServiceManageService.getTeamService(serviceId)))

    override fun createTeamService(
        request: AdminTeamServiceCreateRequest,
        thumbnailImage: MultipartFile?
    ): ResponseEntity<Unit> {
        val serviceId = adminTeamServiceManageService.createTeamService(request, thumbnailImage)
        return ResponseEntity.created(URI("/admin/v1/team-services/$serviceId")).build()
    }

    override fun updateTeamService(
        request: AdminTeamServiceUpdateRequest,
        thumbnailImage: MultipartFile?
    ): ResponseEntity<Unit> {
        adminTeamServiceManageService.updateTeamService(request, thumbnailImage)
        return ResponseEntity.noContent().build()
    }

    override fun deleteTeamService(serviceId: UUID): ResponseEntity<Unit> {
        adminTeamServiceManageService.deleteTeamService(serviceId)
        return ResponseEntity.noContent().build()
    }
}
