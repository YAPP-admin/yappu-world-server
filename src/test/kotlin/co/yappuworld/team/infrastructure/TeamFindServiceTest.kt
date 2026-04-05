package co.yappuworld.team.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.team.domain.vo.Platform
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

class TeamFindServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository
) : CustomDataJpaTestFeatureSpec({
        val teamFindService = TeamFindService(teamRepository)

        feature("팀 목록 조회") {

            scenario("전체 조회 시 기본 정렬이 적용된다") {
                teamRepository.saveAll(
                    List(15) { index ->
                        getTeamEntityFixture(generation = 35, name = "팀$index")
                    }
                )

                val pageable = PageRequest.of(0, 10)
                val result = teamFindService.findTeams(null, null, pageable)

                result.content shouldHaveSize 10
                result.totalElements shouldBe 15
                result.totalPages shouldBe 2
                result.number shouldBe 0
            }

            scenario("기수로 필터링") {
                repeat(7) { index ->
                    teamRepository.save(getTeamEntityFixture(35, "35기팀$index"))
                }

                repeat(8) { index ->
                    teamRepository.save(getTeamEntityFixture(36, "36기팀$index"))
                }

                val pageable = PageRequest.of(0, 5)
                val result = teamFindService.findTeams(35, null, pageable)

                result.content shouldHaveSize 5
                result.totalElements shouldBe 7
                result.content.all { it.generation == 35 } shouldBe true
            }

            scenario("플랫폼 필터링 시 기수 내림차순, 팀 이름 내림차순으로 정렬") {
                teamRepository.save(getTeamEntityFixture(35, "A팀", hasApp = true))
                teamRepository.save(getTeamEntityFixture(35, "C팀", hasApp = true))
                teamRepository.save(getTeamEntityFixture(37, "B팀", hasApp = true, hasWeb = true))
                teamRepository.save(getTeamEntityFixture(37, "D팀", hasApp = true, hasWeb = true))

                val pageable = PageRequest.of(0, 10)
                val result = teamFindService.findTeams(null, Platform.APP, pageable)

                result.content shouldHaveSize 4
                result.content[0].generation shouldBe 37
                result.content[0].teamName shouldBe "D팀"
                result.content[1].generation shouldBe 37
                result.content[1].teamName shouldBe "B팀"
                result.content[2].generation shouldBe 35
                result.content[2].teamName shouldBe "C팀"
                result.content[3].generation shouldBe 35
                result.content[3].teamName shouldBe "A팀"
            }
        }
    })
