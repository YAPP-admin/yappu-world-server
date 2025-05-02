package co.yappuworld.global.util

import java.time.LocalDate

public operator fun LocalDate.rangeTo(other: LocalDate): LocalDateProgression =
    LocalDateProgression.fromClosedRange(this, other, 1)

public operator fun LocalDate.rangeUntil(other: LocalDate): LocalDateProgression =
    LocalDateProgression.fromClosedRange(this, other.minusDays(1), 1)

public infix fun LocalDate.downTo(to: LocalDate): LocalDateProgression =
    LocalDateProgression.fromClosedRange(this, to, -1)

public fun LocalDateProgression.reversed(): LocalDateProgression =
    LocalDateProgression.fromClosedRange(last, first, -step)

public infix fun LocalDateProgression.step(step: Int): LocalDateProgression {
    require(step > 0) { "간격은 0 이상이어야 합니다." }
    return LocalDateProgression.fromClosedRange(first, last, if (this.step > 0) step else -step)
}

public infix fun LocalDate.until(to: LocalDate): LocalDateProgression = this..to.minusDays(1)
