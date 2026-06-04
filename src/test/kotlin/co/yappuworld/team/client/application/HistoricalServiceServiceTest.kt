package co.yappuworld.team.client.application

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceImageEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.support.storage.FakeObjectStorageService
import co.yappuworld.team.client.dto.request.HistoricalServicesPageRequest
import co.yappuworld.team.domain.vo.Platform
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.TeamServiceImageFindService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceImageRepository
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class HistoricalServiceServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val teamServiceRepository: TeamServiceRepository,
    private val teamServiceImageRepository: TeamServiceImageRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({
        lateinit var historicalServiceService: HistoricalServiceService

        beforeEach {
            teamServiceImageRepository.deleteAllInBatch()
            teamMemberRepository.deleteAllInBatch()
            teamServiceRepository.deleteAllInBatch()
            teamRepository.deleteAllInBatch()
            historicalServiceService = HistoricalServiceService(
                teamServiceFindService = TeamServiceFindService(teamServiceRepository),
                teamMemberFindService = TeamMemberFindService(teamMemberRepository),
                teamServiceImageFindService = TeamServiceImageFindService(teamServiceImageRepository),
                objectStorageService = FakeObjectStorageService()
            )
        }

        feature("역대 서비스 목록 조회") {
            scenario("기수와 플랫폼 필터를 적용해 목록을 조회한다") {
                val targetTeam = teamRepository.save(getTeamEntityFixture(generation = 25, name = "타겟팀"))
                val filteredOutTeam = teamRepository.save(getTeamEntityFixture(generation = 24, name = "제외팀"))

                val targetService = teamServiceRepository.save(
                    getTeamServiceEntityFixture(
                        team = targetTeam,
                        name = "타겟 서비스",
                        hasApp = true,
                        summary = "한 줄 소개"
                    )
                )
                teamServiceImageRepository.save(
                    getTeamServiceImageEntityFixture(
                        teamService = targetService,
                        objectKey = "team-services/target.png"
                    )
                )
                teamServiceRepository.save(
                    getTeamServiceEntityFixture(
                        team = filteredOutTeam,
                        name = "제외 서비스",
                        hasWeb = true
                    )
                )

                val result = historicalServiceService.getHistoricalServices(
                    HistoricalServicesPageRequest(generation = 25, platform = Platform.APP)
                )

                result.data shouldHaveSize 1
                result.data.single().serviceName shouldBe "타겟 서비스"
                result.data.single().summary shouldBe "한 줄 소개"
                result.data.single().thumbnailImageUrl shouldBe "https://image.yapp.co.kr/team-services/target.png"
                result.limit shouldBe 20
                result.hasNext shouldBe false
            }

            scenario("커서 기반으로 다음 페이지를 조회한다") {
                val web25Team = teamRepository.save(getTeamEntityFixture(generation = 25, name = "25웹팀"))
                val app25Team = teamRepository.save(getTeamEntityFixture(generation = 25, name = "25앱팀"))
                val app24Team = teamRepository.save(getTeamEntityFixture(generation = 24, name = "24앱팀"))

                val web25Service = teamServiceRepository.save(
                    getTeamServiceEntityFixture(team = web25Team, name = "25웹", hasWeb = true)
                )
                teamServiceRepository.save(
                    getTeamServiceEntityFixture(team = app25Team, name = "25앱", hasApp = true)
                )
                teamServiceRepository.save(
                    getTeamServiceEntityFixture(team = app24Team, name = "24앱", hasApp = true)
                )

                val firstPage = historicalServiceService.getHistoricalServices(
                    HistoricalServicesPageRequest(limit = 2)
                )

                firstPage.data shouldHaveSize 2
                firstPage.data.map { it.serviceName } shouldBe listOf("25앱", "25웹")
                firstPage.lastCursor shouldBe web25Service.id
                firstPage.hasNext shouldBe true

                val secondPage = historicalServiceService.getHistoricalServices(
                    HistoricalServicesPageRequest(lastCursorId = firstPage.lastCursor, limit = 2)
                )

                secondPage.data shouldHaveSize 1
                secondPage.data.single().serviceName shouldBe "24앱"
                secondPage.hasNext shouldBe false
            }
        }

        feature("역대 서비스 상세 조회") {
            scenario("서비스 상세와 팀원 목록을 함께 조회한다") {
                val team = teamRepository.save(getTeamEntityFixture(generation = 17, name = "상세팀"))
                val service = teamServiceRepository.save(
                    getTeamServiceEntityFixture(
                        team = team,
                        name = "상세 서비스",
                        hasWeb = true,
                        webLink = "https://yapp.co.kr",
                        summary = "짧은 설명",
                        description = "긴 설명"
                    )
                )
                val pmUser = userRepository.save(getUserEntityFixture(name = "김피엠"))
                val serverUser = userRepository.save(getUserEntityFixture(name = "김서버"))
                val pmActivityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        userId = pmUser.id,
                        generation = 17,
                        position = Position.PM
                    )
                )
                val serverActivityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        userId = serverUser.id,
                        generation = 17,
                        position = Position.SERVER
                    )
                )
                teamMemberRepository.save(TeamMemberEntity(team = team, activityUnit = serverActivityUnit))
                teamMemberRepository.save(TeamMemberEntity(team = team, activityUnit = pmActivityUnit))
                teamServiceImageRepository.save(
                    getTeamServiceImageEntityFixture(
                        teamService = service,
                        objectKey = "team-services/detail.png"
                    )
                )

                val result = historicalServiceService.getHistoricalServiceDetail(service.id)

                result.generation shouldBe 17
                result.serviceName shouldBe "상세 서비스"
                result.webLink shouldBe "https://yapp.co.kr"
                result.thumbnailImageUrl shouldBe "https://image.yapp.co.kr/team-services/detail.png"
                result.members shouldHaveSize 2
                result.members[0].position shouldBe "PM"
                result.members[0].name shouldBe "김피엠"
                result.members[1].position shouldBe "Server"
                result.members[1].name shouldBe "김서버"
            }
        }
    })
