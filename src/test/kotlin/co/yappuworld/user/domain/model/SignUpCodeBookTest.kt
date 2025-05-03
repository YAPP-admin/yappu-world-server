package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class SignUpCodeBookTest :
    FeatureSpec({

        val configs = UserRole.entries.sortedBy { it.ordinal }.mapIndexed { index, role ->
            ConfigEntity(
                name = role.signUpCodeKey,
                value = "00000$index",
                label = "가입 코드",
                category = ConfigCategory.ATTENDANCE_CODE
            )
        }

        feature("초기화 시") {

            scenario("UserRole 중 존재하지 않는 가입 코드가 있을 경우") {
                forAll(
                    row(UserRole.ADMIN),
                    row(UserRole.STAFF),
                    row(UserRole.ACTIVE),
                    row(UserRole.ALUMNI),
                    row(UserRole.GRADUATE)
                ) { role ->
                    val withoutOneRole = UserRole.entries
                        .filterNot { it == role }
                        .map { entry ->
                            ConfigEntity(
                                name = entry.signUpCodeKey,
                                value = entry.ordinal.toString().padStart(6, '0'),
                                label = "가입 코드",
                                category = ConfigCategory.ATTENDANCE_CODE
                            )
                        }

                    shouldThrowExactly<BusinessException> {
                        SignUpCodeBook(withoutOneRole)
                    }.error shouldBe UserError.SIGN_UP_CODE_UNREGISTERED
                }
            }

            scenario("중복된 가입코드가 있으면 예외가 발생한다.") {
                val configs = UserRole.entries
                    .map { entry ->
                        ConfigEntity(
                            name = entry.signUpCodeKey,
                            value = "000000",
                            label = "가입 코드",
                            category = ConfigCategory.ATTENDANCE_CODE
                        )
                    }

                shouldThrowExactly<BusinessException> {
                    SignUpCodeBook(configs)
                }.error shouldBe UserError.SIGN_UP_CODE_DUPLICATED
            }

            scenario("가입코드가 NULL인 경우 중복되어도 예외가 발생하지 않는다.") {
                val configs = UserRole.entries
                    .map { entry ->
                        ConfigEntity(
                            name = entry.signUpCodeKey,
                            value = null,
                            label = "가입 코드",
                            category = ConfigCategory.ATTENDANCE_CODE
                        )
                    }

                shouldNotThrowAny { SignUpCodeBook(configs) }
            }

            scenario("가입코드가 여섯자리가 아니면 예외가 발생한다.") {
                val configs = UserRole.entries
                    .map { entry ->
                        ConfigEntity(
                            name = entry.signUpCodeKey,
                            value = "00000",
                            label = "가입 코드",
                            category = ConfigCategory.ATTENDANCE_CODE
                        )
                    }

                shouldThrowExactly<BusinessException> {
                    SignUpCodeBook(configs)
                }.error shouldBe UserError.INVALID_SIGN_UP_CODE
            }
        }

        feature("가입코드 수정 시") {

            scenario("가입코드가 중복되면 예외가 발생한다.") {
                val signUpCodeBook = SignUpCodeBook(configs)

                shouldThrowExactly<BusinessException> {
                    signUpCodeBook.updateSignUpCode(
                        UserRole.STAFF,
                        signUpCodeBook.getCode(UserRole.ADMIN)!!
                    )
                }
            }

            scenario("가입코드가 6자리가 아니면 예외가 발생한다.") {
                val signUpCodeBook = SignUpCodeBook(configs)

                shouldThrowExactly<BusinessException> {
                    signUpCodeBook.updateSignUpCode(UserRole.ADMIN, "00000")
                }.error shouldBe UserError.INVALID_SIGN_UP_CODE
            }

            scenario("가입코드가 6자리이면 정상적으로 수정된다.") {
                val signUpCodeBook = SignUpCodeBook(configs)

                shouldNotThrowAny { signUpCodeBook.updateSignUpCode(UserRole.ADMIN, "123456") }
            }
        }

        feature("초기화") {

            scenario("초기화 하면 가입 코드가 NULL로 설정된다.") {
                val signUpCodeBook = SignUpCodeBook(configs)
                forAll(
                    row(UserRole.ADMIN),
                    row(UserRole.STAFF),
                    row(UserRole.ACTIVE),
                    row(UserRole.ALUMNI),
                    row(UserRole.GRADUATE)
                ) { role ->
                    signUpCodeBook.getCode(role).shouldNotBeNull()
                    signUpCodeBook.initializeSignUpCode(role)
                    signUpCodeBook.getCode(role) shouldBe null
                }
            }
        }
    })
