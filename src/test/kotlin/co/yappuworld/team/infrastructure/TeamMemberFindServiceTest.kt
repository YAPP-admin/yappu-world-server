package co.yappuworld.team.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.support.fixture.UserFixture.getActivityUnitEntityFixture
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.jpa.TeamMemberRepository
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.jpa.UserRepository
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class TeamMemberFindServiceTest @Autowired constructor(
    private val teamMemberRepository: TeamMemberRepository,
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({
        val teamMemberFindService = TeamMemberFindService(teamMemberRepository)

        feature("팀 멤버 상세 정보 조회") {

            scenario("팀 ID로 멤버 상세 정보 조회") {
                val team = teamRepository.save(getTeamEntityFixture(generation = 36, name = "팀"))
                val user = userRepository.save(getUserEntityFixture(name = "홍길동"))
                val activityUnit = activityUnitRepository.save(
                    getActivityUnitEntityFixture(userId = user.id, generation = 36, position = Position.IOS)
                )

                teamMemberRepository.save(TeamMemberEntity(team = team, activityUnit = activityUnit))

                val result = teamMemberFindService.findTeamMembersDetail(team.id)

                result shouldHaveSize 1
                result.filterNotNull() shouldHaveSize 1

                val member1 = result.find { it?.userName == "홍길동" }
                member1?.userId shouldBe user.id
                member1?.generation shouldBe 36
                member1?.position shouldBe Position.IOS
                member1?.activityUnitId shouldBe activityUnit.id
            }
        }
    })
