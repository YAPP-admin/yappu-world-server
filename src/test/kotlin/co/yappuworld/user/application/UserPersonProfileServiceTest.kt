package co.yappuworld.user.application

import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.OperationFixture.getGenerationFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamServiceEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import co.yappuworld.user.client.application.UserPersonProfileService
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.UserFindService
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class UserPersonProfileServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val generationRepository: GenerationRepository,
    private val teamRepository: TeamRepository,
    private val teamServiceRepository: TeamServiceRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext
) : CustomDataJpaTestFeatureSpec({
        lateinit var userPersonProfileService: UserPersonProfileService

        beforeEach {
            userPersonProfileService = UserPersonProfileService(
                userFindService = UserFindService(userRepository, entityManager, context),
                teamServiceFindService = TeamServiceFindService(teamServiceRepository)
            )
        }

        feature("공개 유저 프로필 조회") {
            scenario("가장 최근 활동 정보와 역대 서비스 이력을 함께 조회한다") {
                val user = userRepository.save(getUserEntityFixture(name = "김뿌야"))
                generationRepository.save(
                    getGenerationFixture(
                        value = 125,
                        startDate = LocalDate.of(2023, 11, 1),
                        endDate = LocalDate.of(2024, 6, 30)
                    )
                )
                generationRepository.save(
                    getGenerationFixture(
                        value = 120,
                        startDate = LocalDate.of(2018, 11, 1),
                        endDate = LocalDate.of(2019, 6, 30)
                    )
                )
                val latestActivityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        userId = user.id,
                        generation = 125,
                        position = Position.DESIGN
                    )
                )
                val staffActivityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        userId = user.id,
                        generation = 120,
                        position = Position.STAFF
                    )
                )
                val latestTeam = teamRepository.save(
                    getTeamEntityFixture(
                        generation = 125,
                        name = "팀 이름",
                        hasApp = true,
                        hasWeb = true
                    )
                )
                val service = teamServiceRepository.save(
                    getTeamServiceEntityFixture(
                        team = latestTeam,
                        name = "서비스명",
                        hasApp = true,
                        hasWeb = true,
                        googlePlayLink = "https://play.google.com/store/apps/details?id=com.example",
                        appStoreLink = "https://apps.apple.com/app/id123456789",
                        webLink = "https://example.com",
                        summary = "무수무수한 서비스 두줄까지 들어갈것 같아요"
                    )
                )
                teamMemberRepository.save(TeamMemberEntity(team = latestTeam, activityUnit = latestActivityUnit))

                val result = userPersonProfileService.getUserPersonProfile(user.id)

                result.userId shouldBe user.id
                result.name shouldBe "김뿌야"
                result.role shouldBe "활동회원"
                result.latestActivity?.generation shouldBe 125
                result.latestActivity?.position shouldBe "Design"
                result.histories shouldHaveSize 2
                result.histories[0].generation shouldBe 125
                result.histories[0].position shouldBe "Design"
                result.histories[0].activityStartDate shouldBe LocalDate.of(2023, 11, 1)
                result.histories[0].activityEndDate shouldBe LocalDate.of(2024, 6, 30)
                result.histories[0].service?.serviceId shouldBe service.id
                result.histories[0].service?.teamName shouldBe "팀 이름"
                result.histories[0].service?.serviceName shouldBe "서비스명"
                result.histories[0].service?.hasApp shouldBe true
                result.histories[0].service?.hasWeb shouldBe true
                result.histories[0].service?.thumbnailImageUrl shouldBe null
                result.histories[1].generation shouldBe staffActivityUnit.generation
                result.histories[1].position shouldBe "운영진"
                result.histories[1].activityStartDate shouldBe LocalDate.of(2018, 11, 1)
                result.histories[1].activityEndDate shouldBe LocalDate.of(2019, 6, 30)
                result.histories[1].service shouldBe null
            }

            scenario("같은 기수와 같은 포지션의 활동 이력도 최신 활동 기준이 흔들리지 않는다") {
                val user = userRepository.save(getUserEntityFixture(name = "김뿌야"))
                generationRepository.save(
                    getGenerationFixture(
                        value = 125,
                        startDate = LocalDate.of(2023, 11, 1),
                        endDate = LocalDate.of(2024, 6, 30)
                    )
                )
                val firstActivityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        userId = user.id,
                        generation = 125,
                        position = Position.DESIGN
                    )
                )
                val secondActivityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(
                        userId = user.id,
                        generation = 125,
                        position = Position.DESIGN
                    )
                )
                val team = teamRepository.save(
                    getTeamEntityFixture(
                        generation = 125,
                        name = "팀 이름",
                        hasApp = true,
                        hasWeb = false
                    )
                )
                teamServiceRepository.save(
                    getTeamServiceEntityFixture(
                        team = team,
                        name = "서비스명",
                        hasApp = true,
                        hasWeb = false
                    )
                )
                teamMemberRepository.save(TeamMemberEntity(team = team, activityUnit = secondActivityUnit))

                val result = userPersonProfileService.getUserPersonProfile(user.id)
                val expectedLatestActivityUnitId = maxOf(firstActivityUnit.id, secondActivityUnit.id)

                result.latestActivity?.generation shouldBe 125
                result.latestActivity?.position shouldBe "Design"
                result.histories shouldHaveSize 2
                result.histories.first().activityStartDate shouldBe LocalDate.of(2023, 11, 1)
                result
                    .histories
                    .first()
                    .service
                    ?.serviceName shouldBe when (expectedLatestActivityUnitId) {
                    secondActivityUnit.id -> "서비스명"
                    else -> null
                }
            }
        }
    })
