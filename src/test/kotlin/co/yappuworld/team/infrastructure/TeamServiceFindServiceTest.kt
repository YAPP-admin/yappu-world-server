package co.yappuworld.team.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
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
        }
    })
