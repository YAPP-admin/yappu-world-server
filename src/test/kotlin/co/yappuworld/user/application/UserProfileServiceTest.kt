package co.yappuworld.user.application

import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.fixture.OperationFixture.getGenerationFixture
import co.yappuworld.support.fixture.ActivityUnitFixture.getActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserFixture
import co.yappuworld.user.client.application.UserProfileService
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.util.UUID

class UserProfileServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val activityUnitRepository = mockk<ActivityUnitRepository>()
    private val generationRepository = mockk<GenerationRepository>()

    private val userProfileService = UserProfileService(
        userRepository = userRepository,
        activityUnitRepository = activityUnitRepository,
        generationRepository = generationRepository
    )

    @Test
    fun `활동 기수의 시작일과 종료일은 Generation 데이터 등록 여부에 따라 달라진다`() {
        every { userRepository.findByIdOrNull(any()) } returns getUserFixture()
        every { activityUnitRepository.findAllByUserId(any()) } returns listOf(
            getActivityUnitFixture(generation = 23),
            getActivityUnitFixture(generation = 25)
        )

        val startOfTwentyFive = LocalDate.of(2019, 12, 31)
        val endOfTwentyFive = LocalDate.of(2020, 3, 20)
        every {
            generationRepository.findAllByValueIn(listOf(23, 25))
        } returns listOf(
            getGenerationFixture(
                value = 23,
                startDate = null,
                endDate = null
            ),
            getGenerationFixture(
                value = 25,
                startDate = startOfTwentyFive,
                endDate = endOfTwentyFive
            ).apply { activate() }
        )

        val response = userProfileService.findUserActivityHistories(UUID.randomUUID())
        val twentyThree = response.activityUnits.single { it.generation == 23 }
        val twentyFive = response.activityUnits.single { it.generation == 25 }

        assertThat(twentyThree.activityStartDate).isNull()
        assertThat(twentyThree.activityEndDate).isNull()
        assertThat(twentyFive.activityStartDate).isEqualTo(startOfTwentyFive)
        assertThat(twentyFive.activityEndDate).isEqualTo(endOfTwentyFive)
    }
}
