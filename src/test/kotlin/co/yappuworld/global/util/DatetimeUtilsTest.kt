package co.yappuworld.global.util

import co.yappuworld.global.util.DatetimeUtils.dDayFrom
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class DatetimeUtilsTest :
    FeatureSpec({

        feature("- 연산자 테스트") {

            scenario("미래 - 과거") {
                val dDay = LocalDate.of(2024, 12, 12)
                dDay.dDayFrom(LocalDate.of(2024, 12, 11)) shouldBe -1
            }

            scenario("과거 - 미래") {
                val dDay = LocalDate.of(2024, 12, 11)
                dDay.dDayFrom(LocalDate.of(2024, 12, 12)) shouldBe 1
            }

            scenario("같은 날") {
                val dDay = LocalDate.of(2024, 12, 11)
                dDay.dDayFrom(LocalDate.of(2024, 12, 11)) shouldBe 0
            }
        }
    })
