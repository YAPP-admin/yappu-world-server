package co.yappuworld.team.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.team.domain.vo.ServicePlatform
import co.yappuworld.team.infrastructure.entity.ServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.jpa.ServiceRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals

@CustomDataJpaTest
class TeamFindServiceTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val serviceRepository: ServiceRepository
) : CustomDataJpaTestFeatureSpec({

        val teamFindService = TeamFindService(teamRepository)

        feature("팀 목록 조회") {

            scenario("전체 조회 시 기본 정렬이 적용된다") {
                repeat(15) { index ->
                    val service = serviceRepository.save(
                        ServiceEntity(name = null, hasApp = true, hasWeb = false)
                    )
                    teamRepository.save(
                        TeamEntity(generation = 35, name = "팀$index", service = service)
                    )
                }

                val pageable = PageRequest.of(0, 10)
                val result = teamFindService.findTeams(null, null, pageable)

                assertEquals(10, result.content.size)
                assertEquals(15, result.totalElements.toInt())
                assertEquals(2, result.totalPages)
                assertEquals(0, result.number)
            }

            scenario("기수로 필터링") {
                repeat(7) { index ->
                    val service = serviceRepository.save(
                        ServiceEntity(name = null, hasApp = false, hasWeb = false)
                    )
                    teamRepository.save(
                        TeamEntity(generation = 35, name = "35기팀$index", service = service)
                    )
                }
                repeat(8) { index ->
                    val service = serviceRepository.save(
                        ServiceEntity(name = null, hasApp = false, hasWeb = false)
                    )
                    teamRepository.save(
                        TeamEntity(generation = 36, name = "36기팀$index", service = service)
                    )
                }

                val pageable = PageRequest.of(0, 5)
                val result = teamFindService.findTeams(35, null, pageable)

                assertEquals(5, result.content.size)
                assertEquals(7, result.totalElements.toInt())
                result.content.all { it.generation == 35 } shouldBe true
            }

            scenario("플랫폼 필터링 시 기수 내림차순, 팀 이름 내림차순으로 정렬") {
                val service1 = serviceRepository.save(
                    ServiceEntity(name = null, hasApp = true, hasWeb = false)
                )
                teamRepository.save(
                    TeamEntity(generation = 35, name = "A팀", service = service1)
                )

                val service2 = serviceRepository.save(
                    ServiceEntity(name = null, hasApp = true, hasWeb = false)
                )
                teamRepository.save(
                    TeamEntity(generation = 35, name = "C팀", service = service2)
                )

                val service3 = serviceRepository.save(
                    ServiceEntity(name = null, hasApp = true, hasWeb = true)
                )
                teamRepository.save(
                    TeamEntity(generation = 37, name = "B팀", service = service3)
                )

                val service4 = serviceRepository.save(
                    ServiceEntity(name = null, hasApp = true, hasWeb = true)
                )
                teamRepository.save(
                    TeamEntity(generation = 37, name = "D팀", service = service4)
                )

                val pageable = PageRequest.of(0, 10)
                val result = teamFindService.findTeams(null, ServicePlatform.APP, pageable)

                assertEquals(4, result.content.size)
                assertEquals(37, result.content[0].generation)
                assertEquals("D팀", result.content[0].name)
                assertEquals(37, result.content[1].generation)
                assertEquals("B팀", result.content[1].name)
                assertEquals(35, result.content[2].generation)
                assertEquals("C팀", result.content[2].name)
                assertEquals(35, result.content[3].generation)
                assertEquals("A팀", result.content[3].name)
            }
        }
    })
