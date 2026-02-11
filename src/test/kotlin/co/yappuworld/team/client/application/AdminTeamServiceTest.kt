package co.yappuworld.team.client.application

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture
import co.yappuworld.support.fixture.UserFixture
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamCommandService
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.TeamMemberCommandService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.TeamServiceCommandService
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.ActivityUnitFindService
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AdminTeamServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val serviceRepository: TeamServiceRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({

        data class UserTeamResponse(
            val id: UUID,
            val name: String
        )

        lateinit var adminTeamService: AdminTeamService
        lateinit var activityUnit: ActivityUnitEntity

        beforeEach {
            val teamFindService = TeamFindService(teamRepository)
            val teamMemberFindService = TeamMemberFindService(teamMemberRepository)
            val teamCommandService = TeamCommandService(teamRepository)
            val teamServiceFindService = TeamServiceFindService(serviceRepository)
            val serviceCommandService = TeamServiceCommandService(serviceRepository)
            val teamMemberCommandService = TeamMemberCommandService(teamMemberRepository)
            val activityUnitFindService = ActivityUnitFindService(activityUnitRepository)

            adminTeamService = AdminTeamService(
                teamFindService,
                teamMemberFindService,
                teamCommandService,
                teamServiceFindService,
                serviceCommandService,
                teamMemberCommandService,
                activityUnitFindService
            )

            activityUnit = activityUnitRepository.save(
                UserFixture.getActivityUnitEntityFixture(
                    generation = 35,
                    position = Position.IOS
                )
            )
        }

        fun getTeamByActivityUnit(activityUnit: ActivityUnitEntity): UserTeamResponse? {
            val teamMember = activityUnit.teamMember ?: return null

            return UserTeamResponse(
                id = teamMember.team.id,
                name = teamMember.team.name
            )
        }

        fun getTeamsByGeneration(generation: Int): List<UserTeamResponse> =
            teamRepository
                .findAll()
                .filter { it.generation == generation }
                .map {
                    UserTeamResponse(
                        id = it.id,
                        name = it.name
                    )
                }

        feature("개인 팀 관리") {

            scenario("사용자 활동 내역에서 팀 추가") {
                val team = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "A팀"))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team, hasApp = true))

                adminTeamService.assignMemberToTeam(activityUnit, team.id)

                val result = getTeamByActivityUnit(activityUnit)
                result.shouldNotBeNull()
                result.id shouldBe team.id
                result.name shouldBe team.name
            }

            scenario("사용자 활동 내역에서 팀 변경") {
                val team1 = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "팀1"))
                val team2 = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "팀2"))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team1, "서비스1", hasApp = true))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team2, "서비스2", hasWeb = true))

                adminTeamService.assignMemberToTeam(activityUnit, team1.id)
                adminTeamService.assignMemberToTeam(activityUnit, team2.id)

                val result = getTeamByActivityUnit(activityUnit)
                result.shouldNotBeNull()
                result.id shouldBe team2.id
            }

            scenario("사용자 활동 내역에서 팀 삭제") {
                val team = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "테스트팀"))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team, "테스트 서비스", hasApp = true))

                adminTeamService.assignMemberToTeam(activityUnit, team.id)
                adminTeamService.assignMemberToTeam(activityUnit, null)

                val result = getTeamByActivityUnit(activityUnit)
                result.shouldBeNull()
            }
        }

        feature("사용자 활동 내역으로 팀 조회") {

            scenario("사용자 활동 내역으로 팀 조회") {
                val team = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "테스트팀"))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team, "테스트 서비스", hasApp = true))

                adminTeamService.assignMemberToTeam(activityUnit, team.id)

                val result = getTeamByActivityUnit(activityUnit)
                result.shouldNotBeNull()
                result.id shouldBe team.id
                result.name shouldBe team.name
            }

            scenario("팀이 할당되지 않은 활동 내역은 null 반환") {
                val result = getTeamByActivityUnit(activityUnit)

                result.shouldBeNull()
            }
        }

        feature("기수로 팀 목록 조회") {

            scenario("특정 기수의 팀 목록을 조회") {
                val team1 = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "35기팀1"))
                val team2 = teamRepository.save(TeamFixture.getTeamEntityFixture(35, "35기팀2"))
                val team3 = teamRepository.save(TeamFixture.getTeamEntityFixture(36, "36기팀"))

                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team1, "서비스1", hasApp = true))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team2, "서비스2", hasWeb = true))
                serviceRepository.save(TeamFixture.getTeamServiceEntityFixture(team3, "서비스3", hasApp = true))

                val result = getTeamsByGeneration(35)

                result shouldHaveSize 2
            }

            scenario("팀이 없는 기수는 빈 리스트를 반환") {
                val result = getTeamsByGeneration(99)

                result shouldHaveSize 0
            }
        }
    })
