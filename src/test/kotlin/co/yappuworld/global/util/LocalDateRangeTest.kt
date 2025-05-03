package co.yappuworld.global.util

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class LocalDateRangeTest :
    FeatureSpec({

        feature("in이 잘 사용되는가") {

            scenario("정방향") {
                val condition = LocalDate.of(2024, 12, 11) in LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 12)
                condition.shouldBeTrue()
            }

            scenario("비어있는 범위에 하면 FALSE") {
                val condition = LocalDate.of(2024, 12, 11) in LocalDate.of(2024, 12, 12)..LocalDate.of(2024, 12, 10)
                condition.shouldBeFalse()
            }

            scenario("역방향") {
                3 downTo 2
                val condition = LocalDate.of(2024, 12, 11) in
                    LocalDate.of(2024, 12, 12) downTo LocalDate.of(2024, 12, 10)
                condition.shouldBeTrue()
            }
        }

        feature("isEmpty") {

            scenario("범위가 거꾸로면 isEmpty가 true") {
                val range = LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 9)
                range.isEmpty().shouldBeTrue()
            }

            scenario("범위가 정방향이면 isEmpty가 false") {
                val range = LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 11)
                range.isEmpty().shouldBeFalse()
            }
        }

        feature("first, last는 시작과 끝 값이다.") {

            scenario("정방향") {
                val range = LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 11)
                range.first shouldBe LocalDate.of(2024, 12, 10)
                range.last shouldBe LocalDate.of(2024, 12, 11)
            }

            scenario("역방향") {
                val range = LocalDate.of(2024, 12, 11) downTo LocalDate.of(2024, 12, 10)
                range.first shouldBe LocalDate.of(2024, 12, 11)
                range.last shouldBe LocalDate.of(2024, 12, 10)
            }
        }

        feature("for-iterator 사용 가능") {

            scenario("step이 1이면 하루씩 이동") {
                var dateCount = 0
                for (date in LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 20) step 1) {
                    dateCount++
                }

                dateCount shouldBe 11
            }

            scenario("step이 2면 이틀씩 이동") {
                var dateCount = 0
                for (date in LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 20) step 2) {
                    dateCount++
                }

                dateCount shouldBe 6
            }

            scenario("역방향도 가능") {
                var dateCount = 0
                for (date in LocalDate.of(2024, 12, 20) downTo LocalDate.of(2024, 12, 10) step 2) {
                    dateCount++
                }

                dateCount shouldBe 6
            }
        }

        feature("역순으로 만들기") {

            scenario("reversed") {
                val range = LocalDate.of(2024, 12, 10)..LocalDate.of(2024, 12, 20)
                val reversedRange = range.reversed()

                reversedRange.first shouldBe LocalDate.of(2024, 12, 20)
                reversedRange.last shouldBe LocalDate.of(2024, 12, 10)
            }
        }
    })
