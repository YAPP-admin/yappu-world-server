package co.yappuworld.team.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
import co.yappuworld.team.domain.vo.Platform
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

class TeamServiceFindServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val teamServiceRepository: TeamServiceRepository
) : CustomDataJpaTestFeatureSpec({
        val teamServiceFindService = TeamServiceFindService(teamServiceRepository)

        feature("서비스 목록 조회") {

            scenario("전체 조회 시 기수 내림차순으로 정렬") {
                val team1 = teamRepository.save(getTeamEntityFixture(23, "23기팀"))
                val team2 = teamRepository.save(getTeamEntityFixture(24, "24기팀"))
                teamServiceRepository.save(getTeamServiceEntityFixture(team1, "서비스1", hasApp = true))
                teamServiceRepository.save(getTeamServiceEntityFixture(team2, "서비스2", hasWeb = true))

                val pageable = PageRequest.of(0, 10)
                val result = teamServiceFindService.findTeamServices(null, pageable)

                result.content shouldHaveSize 2
                result.content[0].generation shouldBe 24
                result.content[1].generation shouldBe 23
            }

            scenario("기수로 필터링") {
                repeat(5) { index ->
                    val team = teamRepository.save(getTeamEntityFixture(23, "23기팀$index"))
                    teamServiceRepository.save(getTeamServiceEntityFixture(team, "서비스$index", hasApp = true))
                }
                repeat(3) { index ->
                    val team = teamRepository.save(getTeamEntityFixture(24, "24기팀$index"))
                    teamServiceRepository.save(getTeamServiceEntityFixture(team, "서비스$index", hasWeb = true))
                }

                val pageable = PageRequest.of(0, 10)
                val result = teamServiceFindService.findTeamServices(23, pageable)

                result.content shouldHaveSize 5
                result.totalElements shouldBe 5
                result.content.all { it.generation == 23 } shouldBe true
            }

            scenario("서비스가 없는 경우 빈 리스트 반환") {
                val pageable = PageRequest.of(0, 10)
                val result = teamServiceFindService.findTeamServices(null, pageable)

                result.content shouldHaveSize 0
                result.totalElements shouldBe 0
            }

            scenario("플랫폼으로 역대 서비스를 필터링한다") {
                val appTeam = teamRepository.save(getTeamEntityFixture(25, "앱팀"))
                val webTeam = teamRepository.save(getTeamEntityFixture(25, "웹팀"))
                teamServiceRepository.save(getTeamServiceEntityFixture(appTeam, "앱 서비스", hasApp = true))
                teamServiceRepository.save(getTeamServiceEntityFixture(webTeam, "웹 서비스", hasWeb = true))

                val result = teamServiceFindService.findHistoricalServices(
                    generation = 25,
                    platform = Platform.APP,
                    lastServiceId = null,
                    limit = 20
                )

                result shouldHaveSize 1
                result.single().serviceName shouldBe "앱 서비스"
                result.single().hasApp shouldBe true
            }

            scenario("역대 서비스는 기수 내림차순, 플랫폼 순으로 정렬한다") {
                val web25Team = teamRepository.save(getTeamEntityFixture(25, "25웹팀"))
                val app25Team = teamRepository.save(getTeamEntityFixture(25, "25앱팀"))
                val app24Team = teamRepository.save(getTeamEntityFixture(24, "24앱팀"))

                teamServiceRepository.save(getTeamServiceEntityFixture(web25Team, "25웹", hasWeb = true))
                teamServiceRepository.save(getTeamServiceEntityFixture(app25Team, "25앱", hasApp = true))
                teamServiceRepository.save(getTeamServiceEntityFixture(app24Team, "24앱", hasApp = true))

                val result = teamServiceFindService.findHistoricalServices(
                    generation = null,
                    platform = null,
                    lastServiceId = null,
                    limit = 20
                )

                result.map { it.serviceName } shouldBe listOf("25앱", "25웹", "24앱")
            }

            scenario("WEB 필터 커서 조회 시 hasApp 값이 달라도 다음 페이지가 누락되지 않는다") {
                val firstWebTeam = teamRepository.save(getTeamEntityFixture(25, "첫웹팀", hasApp = false, hasWeb = true))
                val secondWebTeam = teamRepository.save(getTeamEntityFixture(25, "둘웹팀", hasApp = true, hasWeb = true))
                val lowerGenerationWebTeam = teamRepository.save(
                    getTeamEntityFixture(24, "아래웹팀", hasApp = false, hasWeb = true)
                )

                val firstWebService = teamServiceRepository.save(
                    getTeamServiceEntityFixture(firstWebTeam, "B서비스", hasWeb = true)
                )
                teamServiceRepository.save(
                    getTeamServiceEntityFixture(secondWebTeam, "A서비스", hasApp = true, hasWeb = true)
                )
                teamServiceRepository.save(
                    getTeamServiceEntityFixture(lowerGenerationWebTeam, "C서비스", hasWeb = true)
                )

                val firstPage = teamServiceFindService.findHistoricalServices(
                    generation = null,
                    platform = Platform.WEB,
                    lastServiceId = null,
                    limit = 1
                )

                firstPage shouldHaveSize 1
                firstPage.single().serviceName shouldBe "B서비스"

                val secondPage = teamServiceFindService.findHistoricalServices(
                    generation = null,
                    platform = Platform.WEB,
                    lastServiceId = firstWebService.id,
                    limit = 10
                )

                secondPage.map { it.serviceName } shouldBe listOf("A서비스", "C서비스")
            }
        }
    })
