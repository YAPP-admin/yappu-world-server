package co.yappuworld.user.client.dto.request

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.support.fixture.UserDtoFixture.getAdminActivityUnitUpdateRequest
import co.yappuworld.support.fixture.UserDtoFixture.getAdminUserUpdateRequestFixture
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserError
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class AdminUserUpdateRequestTest :
    FeatureSpec({

        feature("전화번호 검증") {

            scenario("전화번호가 유효한 경우") {
                shouldNotThrowAny {
                    getAdminUserUpdateRequestFixture(phoneNumber = "010-2345-6789").checkRequest()
                }
            }

            scenario("전화번호가 유효하지 않은 경우") {
                forAll(
                    row("010-123-4567"),
                    row("010-1234-457"),
                    row("011-1234-4567"),
                    row("02-1234-4567")
                ) { phoneNumber ->
                    shouldThrowExactly<BusinessException> {
                        getAdminUserUpdateRequestFixture(phoneNumber = phoneNumber).checkRequest()
                    }.error shouldBe UserError.WRONG_PHONE_NUMBER
                }
            }
        }

        feature("활동내역 검증") {

            scenario("중복된 기수, 직군이 있으면 에러가 발생한다.") {
                shouldThrowExactly<BusinessException> {
                    getAdminUserUpdateRequestFixture(
                        activityUnits = listOf(
                            getAdminActivityUnitUpdateRequest(generation = 23, position = Position.PM),
                            getAdminActivityUnitUpdateRequest(generation = 23, position = Position.PM)
                        )
                    ).checkRequest()
                }.error shouldBe UserError.DUPLICATE_ACTIVITY_UNIT
            }
        }
    })
