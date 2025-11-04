package co.yappuworld.team.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
import co.yappuworld.support.fixture.TeamFixture.saveTeamsWithServices
import co.yappuworld.team.domain.vo.ServicePlatform
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

@CustomDataJpaTest
class TeamFindServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val serviceRepository: TeamServiceRepository
) : CustomDataJpaTestFeatureSpec({

        val teamFindService = TeamFindService(teamRepository)

        feature("팀 목록 조회") {

            scenario("전체 조회 시 기본 정렬이 적용된다") {
                teamRepository
                    .saveAll(
                        List(15) { index ->
                            getTeamEntityFixture(generation = 35, name = "팀$index")
                        }
                    ).also { teams ->
                        serviceRepository.saveAll(
                            teams.map { team ->
                                getTeamServiceEntityFixture(team = team, hasApp = true)
                            }
                        )
                    }

                val pageable = PageRequest.of(0, 10)
                val result = teamFindService.findTeams(null, null, pageable)

                result.content shouldHaveSize 10
                result.totalElements shouldBe 15
                result.totalPages shouldBe 2
                result.number shouldBe 0
            }

            scenario("기수로 필터링") {
                teamRepository.saveTeamsWithServices(
                    serviceRepository,
                    teams = List(7) { index ->
                        TeamFixture.TeamData(35, "35기팀$index")
                    } + List(8) { index ->
                        TeamFixture.TeamData(36, "36기팀$index")
                    }
                )

                val pageable = PageRequest.of(0, 5)
                val result = teamFindService.findTeams(35, null, pageable)

                result.content shouldHaveSize 5
                result.totalElements shouldBe 7
                result.content.all { it.generation == 35 } shouldBe true
            }

            scenario("플랫폼 필터링 시 기수 내림차순, 팀 이름 내림차순으로 정렬") {
                teamRepository.saveTeamsWithServices(
                    serviceRepository,
                    teams = listOf(
                        TeamFixture.TeamData(35, "A팀", hasApp = true),
                        TeamFixture.TeamData(35, "C팀", hasApp = true),
                        TeamFixture.TeamData(37, "B팀", hasApp = true, hasWeb = true),
                        TeamFixture.TeamData(37, "D팀", hasApp = true, hasWeb = true)
                    )
                )

                val pageable = PageRequest.of(0, 10)
                val result = teamFindService.findTeams(null, ServicePlatform.APP, pageable)

                result.content shouldHaveSize 4
                result.content[0].generation shouldBe 37
                result.content[0].name shouldBe "D팀"
                result.content[1].generation shouldBe 37
                result.content[1].name shouldBe "B팀"
                result.content[2].generation shouldBe 35
                result.content[2].name shouldBe "C팀"
                result.content[3].generation shouldBe 35
                result.content[3].name shouldBe "A팀"
            }
        }
    })
