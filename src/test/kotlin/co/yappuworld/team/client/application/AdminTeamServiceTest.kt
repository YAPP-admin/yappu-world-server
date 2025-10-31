package co.yappuworld.team.client.application

import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamCommandService
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.TeamMemberCommandService
import co.yappuworld.team.infrastructure.ServiceCommandService
import co.yappuworld.team.infrastructure.entity.ServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.jpa.ServiceRepository
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

@CustomDataJpaTest
class AdminTeamServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val serviceRepository: ServiceRepository,
    private val teamMemberRepository: TeamMemberRepository
) : CustomDataJpaTestFeatureSpec({

        lateinit var adminTeamService: AdminTeamService

        beforeEach {
            val teamFindService = TeamFindService(teamRepository)
            val teamMemberFindService = TeamMemberFindService(teamMemberRepository)
            val teamCommandService = TeamCommandService(teamRepository)
            val serviceCommandService = ServiceCommandService(serviceRepository)
            val teamMemberCommandService = TeamMemberCommandService(teamMemberRepository)

            adminTeamService = AdminTeamService(
                teamFindService,
                teamMemberFindService,
                teamCommandService,
                serviceCommandService,
                teamMemberCommandService
            )
        }

        feature("개인 팀 관리") {

            scenario("유저 활동 내역에서 팀 추가") {
                val service = serviceRepository.save(
                    ServiceEntity(name = null, hasApp = true, hasWeb = false)
                )
                val team = teamRepository.save(
                    TeamEntity(generation = 35, name = "A팀", service = service)
                )
                val activityUnitId = UUID.randomUUID()

                adminTeamService.assignTeamToActivityUnit(activityUnitId, team.id)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)
                result.shouldNotBeNull()
                result.id shouldBe team.id
                result.name shouldBe team.name
            }

            scenario("유저 활동 내역에서 팀 변경") {
                val service1 = serviceRepository.save(
                    ServiceEntity(name = "서비스1", hasApp = true, hasWeb = false)
                )
                val team1 = teamRepository.save(
                    TeamEntity(generation = 35, name = "팀1", service = service1)
                )

                val service2 = serviceRepository.save(
                    ServiceEntity(name = "서비스2", hasApp = false, hasWeb = true)
                )
                val team2 = teamRepository.save(
                    TeamEntity(generation = 35, name = "팀2", service = service2)
                )

                val activityUnitId = UUID.randomUUID()
                adminTeamService.assignTeamToActivityUnit(activityUnitId, team1.id)
                adminTeamService.assignTeamToActivityUnit(activityUnitId, team2.id)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)
                result.shouldNotBeNull()
                result.id shouldBe team2.id
            }

            scenario("유저 활동 내역에서 팀 삭제") {
                val service = serviceRepository.save(
                    ServiceEntity(name = "테스트 서비스", hasApp = true, hasWeb = false)
                )
                val team = teamRepository.save(
                    TeamEntity(generation = 35, name = "테스트팀", service = service)
                )
                val activityUnitId = UUID.randomUUID()
                adminTeamService.assignTeamToActivityUnit(activityUnitId, team.id)

                adminTeamService.assignTeamToActivityUnit(activityUnitId, null)

                val result = adminTeamService.getTeamByActivityUnitId(activityUnitId)
                result.shouldBeNull()
            }
        }

        feature("유저 활동 내역으로 팀 조회") {

            scenario("유저 활동 내역으로 팀 조회") {
                val service = serviceRepository.save(
                    ServiceEntity(name = "테스트 서비스", hasApp = true, hasWeb = false)
                )
                val team = teamRepository.save(
                    TeamEntity(generation = 35, name = "테스트팀", service = service)
                )
                val activityUnitId = UUID.randomUUID()
                adminTeamService.assignTeamToActivityUnit(activityUnitId, team.id)

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
                val service1 = serviceRepository.save(
                    ServiceEntity(name = "서비스1", hasApp = true, hasWeb = false)
                )
                teamRepository.save(
                    TeamEntity(generation = 35, name = "35기팀1", service = service1)
                )

                val service2 = serviceRepository.save(
                    ServiceEntity(name = "서비스2", hasApp = false, hasWeb = true)
                )
                teamRepository.save(
                    TeamEntity(generation = 35, name = "35기팀2", service = service2)
                )

                val service3 = serviceRepository.save(
                    ServiceEntity(name = "서비스3", hasApp = true, hasWeb = true)
                )
                teamRepository.save(
                    TeamEntity(generation = 36, name = "36기팀", service = service3)
                )

                val result = adminTeamService.getTeamsByGeneration(35)

                result.size shouldBe 2
                result.all { true } shouldBe true
            }

            scenario("팀이 없는 기수는 빈 리스트를 반환한다") {
                val result = adminTeamService.getTeamsByGeneration(99)

                result.size shouldBe 0
            }
        }
    })
