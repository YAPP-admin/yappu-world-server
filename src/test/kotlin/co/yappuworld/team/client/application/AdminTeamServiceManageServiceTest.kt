package co.yappuworld.team.client.application

import co.yappuworld.external.storage.ObjectStorageTransactionSynchronizer
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceImageEntityFixture
import co.yappuworld.support.storage.FakeObjectStorageService
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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

class AdminTeamServiceManageServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val teamServiceRepository: TeamServiceRepository,
    private val teamServiceImageRepository: TeamServiceImageRepository,
    private val transactionManager: PlatformTransactionManager
) : CustomDataJpaTestFeatureSpec({
        lateinit var adminTeamServiceManageService: AdminTeamServiceManageService
        lateinit var objectStorageService: FakeObjectStorageService
        lateinit var objectStorageTransactionSynchronizer: ObjectStorageTransactionSynchronizer

        beforeEach {
            objectStorageService = FakeObjectStorageService()
            objectStorageTransactionSynchronizer = ObjectStorageTransactionSynchronizer(objectStorageService)
            adminTeamServiceManageService = AdminTeamServiceManageService(
                teamFindService = TeamFindService(teamRepository),
                teamServiceFindService = TeamServiceFindService(teamServiceRepository),
                teamServiceCommandService = TeamServiceCommandService(teamServiceRepository),
                teamServiceImageFindService = TeamServiceImageFindService(teamServiceImageRepository),
                teamServiceImageCommandService = TeamServiceImageCommandService(teamServiceImageRepository),
                objectStorageService = objectStorageService,
                objectStorageTransactionSynchronizer = objectStorageTransactionSynchronizer
            )
        }

        afterEach {
            TransactionTemplate(transactionManager)
                .apply {
                    propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
                }.executeWithoutResult {
                    teamServiceImageRepository.deleteAllInBatch()
                    teamServiceRepository.deleteAllInBatch()
                    teamRepository.deleteAllInBatch()
                }
        }

        fun createRequest(teamId: UUID): AdminTeamServiceCreateRequest =
            AdminTeamServiceCreateRequest(
                teamId = teamId,
                name = "서비스",
                hasApp = true,
                hasWeb = false,
                googlePlayLink = null,
                appStoreLink = null,
                webLink = null,
                summary = null,
                description = null,
                isOperating = true
            )

        fun updateRequest(
            id: UUID,
            teamId: UUID,
            removeThumbnail: Boolean = false
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
                removeThumbnail = removeThumbnail,
                summary = null,
                description = null,
                isOperating = false
            )

        fun thumbnailImage(filename: String = "thumbnail.png"): MockMultipartFile =
            MockMultipartFile(
                "thumbnailImage",
                filename,
                "image/png",
                "thumbnail".toByteArray()
            )

        fun newTransactionTemplate(): TransactionTemplate =
            TransactionTemplate(transactionManager).apply {
                propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
            }

        feature("어드민 팀 서비스 상세 조회") {
            scenario("썸네일 이미지가 있으면 공개 URL로 응답한다") {
                val team = teamRepository.save(getTeamEntityFixture())
                val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                teamServiceImageRepository.save(
                    getTeamServiceImageEntityFixture(
                        teamService = service,
                        objectKey = "team-services/${service.id}/thumbnail.png"
                    )
                )

                val result = adminTeamServiceManageService.getTeamService(service.id)

                result.thumbnailImageUrl shouldBe "https://image.yapp.co.kr/team-services/${service.id}/thumbnail.png"
            }

            scenario("썸네일 이미지가 없으면 null로 응답한다") {
                val team = teamRepository.save(getTeamEntityFixture())
                val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))

                val result = adminTeamServiceManageService.getTeamService(service.id)

                result.thumbnailImageUrl shouldBe null
            }
        }

        feature("어드민 팀 서비스 생성") {
            scenario("썸네일 이미지가 있으면 업로드 URL로 썸네일 이미지를 생성한다") {
                val team = teamRepository.save(getTeamEntityFixture())

                val serviceId = adminTeamServiceManageService.createTeamService(
                    createRequest(teamId = team.id),
                    thumbnailImage("create.png")
                )
                val service = teamServiceRepository.getReferenceById(serviceId)

                val thumbnail = teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service)
                thumbnail?.objectKey?.startsWith("team-services/${service.id}/") shouldBe true
                thumbnail?.objectKey?.endsWith(".png") shouldBe true
                objectStorageService.uploadedObjectKeys shouldBe listOf(thumbnail?.objectKey)
            }

            scenario("썸네일 이미지 저장에 실패하면 업로드한 이미지를 삭제한다") {
                val failingTeamServiceImageCommandService = mockk<TeamServiceImageCommandService>()
                every {
                    failingTeamServiceImageCommandService.save(any())
                } throws IllegalStateException("save failed")
                val service = AdminTeamServiceManageService(
                    teamFindService = TeamFindService(teamRepository),
                    teamServiceFindService = TeamServiceFindService(teamServiceRepository),
                    teamServiceCommandService = TeamServiceCommandService(teamServiceRepository),
                    teamServiceImageFindService = TeamServiceImageFindService(teamServiceImageRepository),
                    teamServiceImageCommandService = failingTeamServiceImageCommandService,
                    objectStorageService = objectStorageService,
                    objectStorageTransactionSynchronizer = objectStorageTransactionSynchronizer
                )
                val transactionTemplate = newTransactionTemplate()

                shouldThrow<IllegalStateException> {
                    transactionTemplate.executeWithoutResult {
                        val team = teamRepository.save(getTeamEntityFixture())
                        service.createTeamService(
                            createRequest(teamId = team.id),
                            thumbnailImage("create.png")
                        )
                    }
                }

                objectStorageService.deletedObjectKeys shouldBe objectStorageService.uploadedObjectKeys
            }

            scenario("썸네일 이미지가 없으면 썸네일 이미지를 생성하지 않는다") {
                val team = teamRepository.save(getTeamEntityFixture())

                val serviceId = adminTeamServiceManageService.createTeamService(
                    createRequest(teamId = team.id),
                    null
                )
                val service = teamServiceRepository.getReferenceById(serviceId)

                teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service) shouldBe null
            }
        }

        feature("어드민 팀 서비스 수정") {
            scenario("기존 썸네일이 있으면 업로드 URL로 변경한다") {
                lateinit var serviceId: UUID
                lateinit var thumbnailObjectKey: String

                newTransactionTemplate().executeWithoutResult {
                    val team = teamRepository.save(getTeamEntityFixture())
                    val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                    serviceId = service.id
                    teamServiceImageRepository.save(
                        getTeamServiceImageEntityFixture(
                            teamService = service,
                            objectKey = "team-services/${service.id}/before.png"
                        )
                    )

                    adminTeamServiceManageService.updateTeamService(
                        updateRequest(
                            id = service.id,
                            teamId = team.id
                        ),
                        thumbnailImage("after.png")
                    )

                    thumbnailObjectKey = teamServiceImageRepository
                        .findByTeamServiceAndIsThumbnailTrue(service)
                        ?.objectKey
                        ?: ""
                }

                thumbnailObjectKey.startsWith("team-services/$serviceId/") shouldBe true
                thumbnailObjectKey.endsWith(".png") shouldBe true
                objectStorageService.deletedObjectKeys shouldBe listOf("team-services/$serviceId/before.png")
            }

            scenario("기존 썸네일 변경 중 트랜잭션이 롤백되면 새 이미지만 삭제한다") {
                shouldThrow<IllegalStateException> {
                    newTransactionTemplate().executeWithoutResult {
                        val team = teamRepository.save(getTeamEntityFixture())
                        val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                        teamServiceImageRepository.save(
                            getTeamServiceImageEntityFixture(
                                teamService = service,
                                objectKey = "team-services/${service.id}/before.png"
                            )
                        )

                        adminTeamServiceManageService.updateTeamService(
                            updateRequest(
                                id = service.id,
                                teamId = team.id
                            ),
                            thumbnailImage("after.png")
                        )
                        throw IllegalStateException("rollback")
                    }
                }

                objectStorageService.deletedObjectKeys shouldBe objectStorageService.uploadedObjectKeys
            }

            scenario("기존 썸네일이 없으면 새 썸네일을 생성한다") {
                val team = teamRepository.save(getTeamEntityFixture())
                val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))

                adminTeamServiceManageService.updateTeamService(
                    updateRequest(
                        id = service.id,
                        teamId = team.id
                    ),
                    thumbnailImage("new.png")
                )

                val thumbnail = teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service)
                thumbnail?.objectKey?.startsWith("team-services/${service.id}/") shouldBe true
                thumbnail?.objectKey?.endsWith(".png") shouldBe true
            }

            scenario("썸네일 이미지와 삭제 요청이 없으면 기존 썸네일을 유지한다") {
                val team = teamRepository.save(getTeamEntityFixture())
                val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                teamServiceImageRepository.save(
                    getTeamServiceImageEntityFixture(
                        teamService = service,
                        objectKey = "team-services/${service.id}/before.png"
                    )
                )

                adminTeamServiceManageService.updateTeamService(
                    updateRequest(
                        id = service.id,
                        teamId = team.id
                    ),
                    null
                )

                val thumbnail = teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service)
                thumbnail?.objectKey shouldBe "team-services/${service.id}/before.png"
            }

            scenario("썸네일 삭제 요청이 있으면 기존 썸네일을 삭제한다") {
                var thumbnailExists = true

                newTransactionTemplate().executeWithoutResult {
                    val team = teamRepository.save(getTeamEntityFixture())
                    val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                    teamServiceImageRepository.save(getTeamServiceImageEntityFixture(teamService = service))

                    adminTeamServiceManageService.updateTeamService(
                        updateRequest(
                            id = service.id,
                            teamId = team.id,
                            removeThumbnail = true
                        ),
                        null
                    )

                    thumbnailExists = teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(service) != null
                }

                thumbnailExists shouldBe false
                objectStorageService.deletedObjectKeys shouldBe listOf("team-services/thumbnail.png")
            }

            scenario("썸네일 삭제 요청 중 트랜잭션이 롤백되면 기존 이미지를 삭제하지 않는다") {
                shouldThrow<IllegalStateException> {
                    newTransactionTemplate().executeWithoutResult {
                        val team = teamRepository.save(getTeamEntityFixture())
                        val service = teamServiceRepository.save(getTeamServiceEntityFixture(team = team))
                        teamServiceImageRepository.save(getTeamServiceImageEntityFixture(teamService = service))

                        adminTeamServiceManageService.updateTeamService(
                            updateRequest(
                                id = service.id,
                                teamId = team.id,
                                removeThumbnail = true
                            ),
                            null
                        )
                        throw IllegalStateException("rollback")
                    }
                }

                objectStorageService.deletedObjectKeys shouldBe emptyList()
            }
        }
    })
