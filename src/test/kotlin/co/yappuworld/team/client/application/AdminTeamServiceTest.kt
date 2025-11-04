package co.yappuworld.team.client.application

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture
import co.yappuworld.support.fixture.TeamFixture.saveTeamWithService
import co.yappuworld.support.fixture.TeamFixture.saveTeamsWithServices
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamCommandService
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.TeamMemberCommandService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.TeamServiceCommandService
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.user.infrastructure.ActivityUnitFindService
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AdminTeamServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val serviceRepository: TeamServiceRepository,
    private val teamMemberRepository: TeamMemberRepository
) : CustomDataJpaTestFeatureSpec({

        lateinit var adminTeamService: AdminTeamService

        beforeEach {
            val teamFindService = TeamFindService(teamRepository)
            val teamMemberFindService = TeamMemberFindService(teamMemberRepository)
            val teamCommandService = TeamCommandService(teamRepository)
            val teamServiceFindService = TeamServiceFindService(serviceRepository)
            val serviceCommandService = TeamServiceCommandService(serviceRepository)
            val teamMemberCommandService = TeamMemberCommandService(teamMemberRepository)

            val activityUnitFindService = mockk<ActivityUnitFindService>(relaxed = true)
            val userFindService = mockk<UserFindService>(relaxed = true)

            adminTeamService = AdminTeamService(
                teamFindService,
                teamMemberFindService,
                teamCommandService,
                teamServiceFindService,
                serviceCommandService,
                teamMemberCommandService,
                activityUnitFindService,
                userFindService
            )
        }

        feature("개인 팀 관리") {

            scenario("사용자 활동 내역에서 팀 추가") {
                val team = teamRepository.saveTeamWithService(
                    serviceRepository,
                    generation = 35,
                    name = "A팀",
                    hasApp = true
                )
                val activityUnitId = UUID.randomUUID()

                adminTeamService.assignMemberToTeam(activityUnitId, team.id)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)
                result.shouldNotBeNull()
                result.id shouldBe team.id
                result.name shouldBe team.name
            }

            scenario("사용자 활동 내역에서 팀 변경") {
                val team1 = teamRepository.saveTeamWithService(
                    serviceRepository,
                    generation = 35,
                    name = "팀1",
                    serviceName = "서비스1",
                    hasApp = true
                )
                val team2 = teamRepository.saveTeamWithService(
                    serviceRepository,
                    generation = 35,
                    name = "팀2",
                    serviceName = "서비스2",
                    hasWeb = true
                )
                val activityUnitId = UUID.randomUUID()

                adminTeamService.assignMemberToTeam(activityUnitId, team1.id)
                adminTeamService.assignMemberToTeam(activityUnitId, team2.id)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)
                result.shouldNotBeNull()
                result.id shouldBe team2.id
            }

            scenario("사용자 활동 내역에서 팀 삭제") {
                val team = teamRepository.saveTeamWithService(
                    serviceRepository,
                    generation = 35,
                    name = "테스트팀",
                    serviceName = "테스트 서비스",
                    hasApp = true
                )
                val activityUnitId = UUID.randomUUID()
                adminTeamService.assignMemberToTeam(activityUnitId, team.id)

                adminTeamService.assignMemberToTeam(activityUnitId, null)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)
                result.shouldBeNull()
            }
        }

        feature("사용자 활동 내역으로 팀 조회") {

            scenario("사용자 활동 내역으로 팀 조회") {
                val team = teamRepository.saveTeamWithService(
                    serviceRepository,
                    generation = 35,
                    name = "테스트팀",
                    serviceName = "테스트 서비스",
                    hasApp = true
                )
                val activityUnitId = UUID.randomUUID()
                adminTeamService.assignMemberToTeam(activityUnitId, team.id)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)

                result.shouldNotBeNull()
                result.id shouldBe team.id
                result.name shouldBe team.name
            }

            scenario("팀이 할당되지 않은 활동 내역은 null 반환") {
                val activityUnitId = UUID.randomUUID()

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)

                result.shouldBeNull()
            }
        }

        feature("기수로 팀 목록 조회") {

            scenario("특정 기수의 팀 목록을 조회한다") {
                teamRepository.saveTeamsWithServices(
                    serviceRepository,
                    teams = listOf(
                        TeamFixture.TeamData(35, "35기팀1", "서비스1", hasApp = true),
                        TeamFixture.TeamData(35, "35기팀2", "서비스2", hasWeb = true),
                        TeamFixture.TeamData(36, "36기팀", "서비스3", hasApp = true)
                    )
                )

                val result = adminTeamService.getTeamsByGeneration(35)

                result shouldHaveSize 2
            }

            scenario("팀이 없는 기수는 빈 리스트를 반환한다") {
                val result = adminTeamService.getTeamsByGeneration(99)

                result shouldHaveSize 0
            }
        }
    })
