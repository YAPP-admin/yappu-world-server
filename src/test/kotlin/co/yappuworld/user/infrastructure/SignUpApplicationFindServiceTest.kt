package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.longs.shouldBeZero
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.util.UUID

class SignUpApplicationFindServiceTest @Autowired constructor(
    private val signUpApplicationRepository: SignUpApplicationRepository
) : CustomDataJpaTestFeatureSpec({

        val signUpApplicationFindService = SignUpApplicationFindService(signUpApplicationRepository)

        val email = "testuser1234@abc.com"

        feature("심사 대기 상태의 가입 신청서가 있는지 조회") {

            scenario("기존에 가입 신청서가 없다면 FALSE") {
                signUpApplicationFindService.existsPendingApplication(email).shouldBeFalse()
            }

            scenario("기존에 처리된 신청서만 있다면 FALSE") {
                signUpApplicationRepository.save(getSignUpApplicationEntityFixture(email = email).apply { approve() })
                signUpApplicationRepository.save(getSignUpApplicationEntityFixture(email = email).apply { reject() })

                signUpApplicationFindService.existsPendingApplication(email).shouldBeFalse()
            }

            scenario("처리되지 않은 신청서가 존재하면 TRUE") {
                signUpApplicationRepository.saveAndFlush(getSignUpApplicationEntityFixture(email = email))
                signUpApplicationFindService.existsPendingApplication(email).shouldBeTrue()
            }

            scenario("처리된 거랑 안 된 거랑 같이 있으면 TRUE") {
                signUpApplicationRepository.save(getSignUpApplicationEntityFixture(email = email).apply { approve() })
                signUpApplicationRepository.save(getSignUpApplicationEntityFixture(email = email).apply { reject() })
                signUpApplicationRepository.save(getSignUpApplicationEntityFixture(email = email))
                signUpApplicationRepository.flush()
                signUpApplicationFindService.existsPendingApplication(email).shouldBeTrue()
            }
        }

        feature("가입 신청서 목록 조회") {

            scenario("빈 리스트로 요청을 하는 경우에 예외가 발생") {
                shouldThrowExactly<IllegalArgumentException> {
                    signUpApplicationFindService.findSignUpApplications(emptyList())
                }
            }

            scenario("존재하지 않는 ID으로만 목록이 이루어지면 빈 리스트가 반환된다.") {
                signUpApplicationFindService
                    .findSignUpApplications(listOf(UUID.randomUUID()))
                    .shouldBeEmpty()
            }

            scenario("요청 목록에 존재하지 않는 ID는 제외하고 조회된다.") {
                val signUpApplication = getSignUpApplicationEntityFixture()
                signUpApplicationRepository.saveAndFlush(signUpApplication)

                signUpApplicationFindService
                    .findSignUpApplications(listOf(UUID.randomUUID(), signUpApplication.id))
                    .shouldHaveSize(1)
            }

            scenario("요청 목록에 존재하는 ID는 모두 조회된다.") {
                val signUpApplications =
                    listOf(getSignUpApplicationEntityFixture(), getSignUpApplicationEntityFixture())
                signUpApplicationRepository.saveAllAndFlush(signUpApplications)

                val result = signUpApplicationFindService.findSignUpApplications(signUpApplications.map { it.id })
                result.shouldHaveSize(2)
                result.forEachIndexed { index, signUpApplication ->
                    signUpApplication.id shouldBe signUpApplications[index].id
                }
            }
        }

        feature("최근의 가입 신청서 조회") {

            scenario("최근 가입 신청이 없는 경우 NULL") {
                signUpApplicationFindService.findLatestSignUpApplication(email).shouldBeNull()
            }

            scenario("하나만 있으면, 하나 반환") {
                val signUpApplication = getSignUpApplicationEntityFixture(email = email)
                signUpApplicationRepository.saveAndFlush(signUpApplication)

                signUpApplicationFindService.findLatestSignUpApplication(email)?.id shouldBe signUpApplication.id
            }

            scenario("여러 개가 있으면, 가장 최근 것 반환") {
                val signUpApplications = listOf(
                    getSignUpApplicationEntityFixture(email = email),
                    getSignUpApplicationEntityFixture(email = email)
                )
                signUpApplicationRepository.saveAllAndFlush(signUpApplications)

                signUpApplicationFindService.findLatestSignUpApplication(email)?.id shouldBe
                    signUpApplications.maxBy { it.createdAt }.id
            }
        }

        feature("ID로 가입 신청서 조회") {

            scenario("존재하지 않으면 예외 발생") {
                shouldThrowExactly<BusinessException> {
                    signUpApplicationFindService.findSignUpApplication(UUID.randomUUID())
                }.error shouldBe UserError.NOT_FOUND_SIGN_UP_APPLICATION
            }

            scenario("존재하면 정상적으로 조회") {
                val signUpApplication = getSignUpApplicationEntityFixture()
                signUpApplicationRepository.saveAndFlush(signUpApplication)

                shouldNotThrowAny {
                    signUpApplicationFindService.findSignUpApplication(signUpApplication.id)
                }
            }
        }

        feature("가입 신청서 페이지 조회") {

            scenario("가입 신청서가 없으면 빈 페이지") {
                val pageRequest = PageRequest.of(0, 10)
                signUpApplicationFindService.findSignUpApplications(pageRequest).let {
                    it.content.shouldBeEmpty()
                    it.totalPages.shouldBeZero()
                    it.totalElements.shouldBeZero()
                    it.number.shouldBeZero()
                    it.numberOfElements.shouldBeZero()
                }
            }

            scenario("가입 신청서가 10개 있을 때, 10개 1페이지 조회하는 경우") {
                val signUpApplications = List(10) { getSignUpApplicationEntityFixture() }
                signUpApplicationRepository.saveAllAndFlush(signUpApplications)

                val pageRequest = PageRequest.of(0, 10)
                signUpApplicationFindService.findSignUpApplications(pageRequest).let {
                    it.content.shouldHaveSize(10)
                    it.totalPages shouldBe 1
                    it.totalElements shouldBe 10
                    it.number.shouldBeZero()
                    it.numberOfElements shouldBe 10
                }
            }

            scenario("가입 신청서가 11개 있을 때, 10개 1페이지 조회하는 경우") {
                val signUpApplications = List(11) { getSignUpApplicationEntityFixture() }
                signUpApplicationRepository.saveAllAndFlush(signUpApplications)

                val pageRequest = PageRequest.of(0, 10)
                signUpApplicationFindService.findSignUpApplications(pageRequest).let {
                    it.content.shouldHaveSize(10)
                    it.totalPages shouldBe 2
                    it.totalElements shouldBe 11
                    it.number.shouldBeZero()
                    it.numberOfElements shouldBe 10
                }
            }

            scenario("가입 신청서가 11개 있을 때, 10개 2페이지 조회하는 경우") {
                val signUpApplications = List(11) { getSignUpApplicationEntityFixture() }
                signUpApplicationRepository.saveAllAndFlush(signUpApplications)

                val pageRequest = PageRequest.of(1, 10)
                signUpApplicationFindService.findSignUpApplications(pageRequest).let {
                    it.content.shouldHaveSize(1)
                    it.totalPages shouldBe 2
                    it.totalElements shouldBe 11
                    it.number shouldBe 1
                    it.numberOfElements shouldBe 1
                }
            }
        }
    })
