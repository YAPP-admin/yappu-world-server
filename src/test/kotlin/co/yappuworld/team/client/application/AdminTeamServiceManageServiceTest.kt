package co.yappuworld.team.client.application

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceImageEntityFixture
import co.yappuworld.team.client.dto.request.AdminTeamServiceCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamServiceUpdateRequest
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamServiceCommandService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.TeamServiceImageCommandService
import co.yappuworld.team.infrastructure.TeamServiceImageFindService
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceImageRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AdminTeamServiceManageServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val teamServiceRepository: TeamServiceRepository,
    private val teamServiceImageRepository: TeamServiceImageRepository
) : CustomDataJpaTestFeatureSpec({
        lateinit var adminTeamServiceManageService: AdminTeamServiceManageService

        beforeEach {
            adminTeamServiceManageService = AdminTeamServiceManageService(
                teamFindService = TeamFindService(teamRepository),
                teamServiceFindService = TeamServiceFindService(teamServiceRepository),
                teamServiceCommandService = TeamServiceCommandService(teamServiceRepository),
                teamServiceImageFindService = TeamServiceImageFindService(teamServiceImageRepository),
                teamServiceImageCommandService = TeamServiceImageCommandService(teamServiceImageRepository)
            )
        }

        fun createRequest(
            teamId: UUID,
            thumbnailImageUrl: String? = null
        ): AdminTeamServiceCreateRequest =
            AdminTeamServiceCreateRequest(
                teamId = teamId,
                name = "서비스",
                hasApp = true,
                hasWeb = false,
                googlePlayLink = null,
                appStoreLink = null,
                webLink = null,
                thumbnailImageUrl = thumbnailImageUrl,
                summary = null,
                description = null,
                isOperating = true
            )

        fun updateRequest(
            id: UUID,
            teamId: UUID,
            thumbnailImageUrl: String? = null
        ): AdminTeamServiceUpdateRequest =
            AdminTeamServiceUpdateRequest(
                id = id,
                teamId = teamId,
                name = "수정 서비스",
                hasApp = false,
                hasWeb = true,
                googlePlayLink = null,
                appStoreLink = null,
                webLink = null,
                thumbnailImageUrl = thumbnailImageUrl,
                summary = null,
                description = null,
                isOperating = false
            )

        feature("어드민 팀 서비스 생성") {
            scenario("썸네일 URL이 있으면 썸네일 이미지를 생성한다") {
                val team = teamRepository.save(getTeamEntityFixture())

                val serviceId = adminTeamServiceManageService.createTeamService(
                    createRequest(
                        teamId = team.id,
                        thumbnailImageUrl = "https://image.yapp.co.kr/create.png"
                    )
                )
                val service = teamServiceRepository.getReferenceById(serviceId)

                val thumbnail = teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service)
                thumbnail?.imageUrl shouldBe "https://image.yapp.co.kr/create.png"
            }

            scenario("썸네일 URL이 없으면 썸네일 이미지를 생성하지 않는다") {
                val team = teamRepository.save(getTeamEntityFixture())

                val serviceId = adminTeamServiceManageService.createTeamService(
                    createRequest(teamId = team.id)
                )
                val service = teamServiceRepository.getReferenceById(serviceId)

                teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service) shouldBe null
            }
        }

        feature("어드민 팀 서비스 수정") {
            scenario("기존 썸네일이 있으면 URL을 변경한다") {
                val team = teamRepository.save(getTeamEntityFixture())
                val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                teamServiceImageRepository.save(
                    getTeamServiceImageEntityFixture(
                        teamService = service,
                        imageUrl = "https://image.yapp.co.kr/before.png"
                    )
                )

                adminTeamServiceManageService.updateTeamService(
                    updateRequest(
                        id = service.id,
                        teamId = team.id,
                        thumbnailImageUrl = "https://image.yapp.co.kr/after.png"
                    )
                )

                val thumbnail = teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service)
                thumbnail?.imageUrl shouldBe "https://image.yapp.co.kr/after.png"
            }

            scenario("썸네일 URL이 없으면 기존 썸네일을 삭제한다") {
                val team = teamRepository.save(getTeamEntityFixture())
                val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                teamServiceImageRepository.save(getTeamServiceImageEntityFixture(teamService = service))

                adminTeamServiceManageService.updateTeamService(
                    updateRequest(
                        id = service.id,
                        teamId = team.id
                    )
                )

                teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service) shouldBe null
            }
        }
    })
