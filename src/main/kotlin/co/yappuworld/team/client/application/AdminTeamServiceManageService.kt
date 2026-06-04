package co.yappuworld.team.client.application

import co.yappuworld.external.storage.ObjectStorageService
import co.yappuworld.external.storage.ObjectStorageTransactionSynchronizer
import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.team.client.dto.request.AdminTeamServiceCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamServicePageRequest
import co.yappuworld.team.client.dto.request.AdminTeamServiceUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamServiceDetailResponse
import co.yappuworld.team.client.dto.response.AdminTeamServiceResponse
import co.yappuworld.team.domain.vo.TeamError
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamServiceCommandService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.TeamServiceImageCommandService
import co.yappuworld.team.infrastructure.TeamServiceImageFindService
import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceImageEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class AdminTeamServiceManageService(
    private val teamFindService: TeamFindService,
    private val teamServiceFindService: TeamServiceFindService,
    private val teamServiceCommandService: TeamServiceCommandService,
    private val teamServiceImageFindService: TeamServiceImageFindService,
    private val teamServiceImageCommandService: TeamServiceImageCommandService,
    private val objectStorageService: ObjectStorageService,
    private val objectStorageTransactionSynchronizer: ObjectStorageTransactionSynchronizer
) {
    @Transactional(readOnly = true)
    fun getTeamServices(request: AdminTeamServicePageRequest): OffsetPageResponse<AdminTeamServiceResponse> {
        val servicesPage = teamServiceFindService.findTeamServices(
            request.generation,
            request.toPageRequest()
        )
        return OffsetPageResponse(
            data = servicesPage.content.map { AdminTeamServiceResponse.from(it) },
            totalCount = servicesPage.totalElements,
            totalPages = servicesPage.totalPages,
            page = request.page,
            size = request.size
        )
    }

    @Transactional(readOnly = true)
    fun getTeamService(serviceId: UUID): AdminTeamServiceDetailResponse {
        val service = teamServiceFindService.findTeamService(serviceId)
        val thumbnailImageUrl = teamServiceImageFindService
            .findThumbnailObjectKey(service)
            ?.let { objectStorageService.getPublicUrl(it) }
        return AdminTeamServiceDetailResponse.from(service, thumbnailImageUrl)
    }

    @Transactional
    fun createTeamService(
        request: AdminTeamServiceCreateRequest,
        thumbnailImage: MultipartFile?
    ): UUID {
        val team = teamFindService.findTeam(request.teamId)
        val service = request.toService(team).also { teamServiceCommandService.save(it) }
        createThumbnail(service, thumbnailImage)
        return service.id
    }

    @Transactional
    fun updateTeamService(
        request: AdminTeamServiceUpdateRequest,
        thumbnailImage: MultipartFile?
    ) {
        val service = teamServiceFindService.findTeamService(request.id)
        val team = teamFindService.findTeam(request.teamId)
        service.update(
            team = team,
            name = request.name,
            hasApp = request.hasApp,
            hasWeb = request.hasWeb,
            serviceLinks = ServiceLinks(
                googlePlay = request.googlePlayLink,
                appStore = request.appStoreLink,
                web = request.webLink
            ),
            summary = request.summary,
            description = request.description,
            isOperating = request.isOperating
        )
        updateThumbnail(service, thumbnailImage, request.removeThumbnail)
    }

    @Transactional
    fun deleteTeamService(serviceId: UUID) {
        val service = teamServiceFindService.findTeamService(serviceId)
        teamServiceImageFindService.findImages(service).forEach { deleteImage(it) }
        teamServiceCommandService.delete(service)
    }

    private fun createThumbnail(
        service: TeamServiceEntity,
        thumbnailImage: MultipartFile?
    ) {
        thumbnailImage ?: return
        val objectKey = buildObjectKey(service.id, thumbnailImage)
        objectStorageService.upload(file = thumbnailImage, objectKey = objectKey)
        objectStorageTransactionSynchronizer.registerDeleteOnRollback(objectKey)
        teamServiceImageCommandService.save(
            TeamServiceImageEntity(teamService = service, objectKey = objectKey, isThumbnail = true)
        )
    }

    private fun updateThumbnail(
        service: TeamServiceEntity,
        thumbnailImage: MultipartFile?,
        removeThumbnail: Boolean
    ) {
        if (thumbnailImage != null && removeThumbnail) {
            throw BusinessException(TeamError.INVALID_THUMBNAIL_REQUEST)
        }

        val thumbnail = teamServiceImageFindService.findThumbnail(service)
        when {
            thumbnailImage != null -> {
                val newObjectKey = buildObjectKey(service.id, thumbnailImage)
                objectStorageService.upload(file = thumbnailImage, objectKey = newObjectKey)
                objectStorageTransactionSynchronizer.registerDeleteOnRollback(newObjectKey)
                thumbnail
                    ?.also {
                        val previousObjectKey = it.objectKey
                        it.updateObjectKey(newObjectKey)
                        objectStorageTransactionSynchronizer.registerDeleteAfterCommit(previousObjectKey)
                    }
                    ?: teamServiceImageCommandService.save(
                        TeamServiceImageEntity(teamService = service, objectKey = newObjectKey, isThumbnail = true)
                    )
            }
            removeThumbnail -> thumbnail?.let { deleteImage(it) }
        }
    }

    private fun deleteImage(image: TeamServiceImageEntity) {
        val objectKey = image.objectKey
        teamServiceImageCommandService.delete(image)
        objectStorageTransactionSynchronizer.registerDeleteAfterCommit(objectKey)
    }

    private fun buildObjectKey(
        serviceId: UUID,
        file: MultipartFile
    ): String {
        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }
            ?: throw BusinessException(TeamError.INVALID_IMAGE)

        if (file.isEmpty || file.size > MAX_IMAGE_FILE_SIZE || extension !in ALLOWED_IMAGE_EXTENSIONS) {
            throw BusinessException(TeamError.INVALID_IMAGE)
        }

        return "team-services/$serviceId/${UUID.randomUUID()}.$extension"
    }

    private companion object {
        const val MAX_IMAGE_FILE_SIZE = 10 * 1024 * 1024L
        val ALLOWED_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
    }
}
