package co.yappuworld.global.util

import co.yappuworld.global.util.StringUtils.isPhoneNumber
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeFalse
import kotlin.test.assertTrue

class StringUtilsTest :
    FeatureSpec({

        feature("핸드폰 번호 정규식 검증") {

            scenario("핸드폰 번호는 통과한다.") {
                assertTrue { "010-1234-5678".isPhoneNumber() }
            }

            scenario("핸드폰 번호가 아닌 애들은 통과하지 않는다.") {
                listOf(
                    "010-1234-567",
                    "010-1234-56789",
                    "01012345678",
                    "010-1234-5678-",
                    "010-1234-5678a",
                    "010-1234-a678"
                ).shouldForAll {
                    it.isPhoneNumber().shouldBeFalse()
                }
            }
        }
    })
