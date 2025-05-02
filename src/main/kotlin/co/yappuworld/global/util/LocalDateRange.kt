package co.yappuworld.global.util

import co.yappuworld.global.util.DatetimeUtils.isBeforeOrEqual
import java.time.LocalDate

class LocalDateRange(
    start: LocalDate,
    endInclusive: LocalDate
) : LocalDateProgression(start, endInclusive, 1),
    ClosedRange<LocalDate>,
    OpenEndRange<LocalDate> {

    override val start: LocalDate get() = first
    override val endInclusive: LocalDate get() = last
    override val endExclusive: LocalDate get() = endInclusive.plusDays(1)

    override fun contains(value: LocalDate): Boolean =
        start.isBeforeOrEqual(value) && value.isBeforeOrEqual(endInclusive)

    override fun isEmpty(): Boolean = start.isAfter(last)

    override fun equals(other: Any?): Boolean =
        other is LocalDateRange &&
            ((isEmpty() && other.isEmpty()) || (start == other.start && endInclusive == other.endInclusive))

    override fun hashCode(): Int = if (isEmpty()) -1 else (31 * (31 * first.hashCode() + last.hashCode()) + step)
}

open class LocalDateProgression
    internal constructor(
        start: LocalDate,
        endInclusive: LocalDate,
        step: Int
    ) : Iterable<LocalDate> {
        init {
            require(step != 0) { "간격은 0일 수 없습니다." }
        }

        public val first: LocalDate = start
        public val last: LocalDate = getProgressionLastElement(start, endInclusive, step)
        public val step: Int = step

        override fun iterator(): Iterator<LocalDate> = LocalDateProgressionIterator(first, last, step)

        public open fun isEmpty(): Boolean = if (step > 0) first > last else first < last

        override fun equals(other: Any?): Boolean =
            other is LocalDateProgression &&
                ((isEmpty() && other.isEmpty()) || (first == other.first && last == other.last && step == other.step))

        override fun hashCode(): Int = if (isEmpty()) -1 else (31 * (31 * first.hashCode() + last.hashCode()) + step)

        public companion object {
            public fun fromClosedRange(
                rangeStart: LocalDate,
                rangeEnd: LocalDate,
                step: Int
            ): LocalDateProgression = LocalDateProgression(rangeStart, rangeEnd, step)
        }
    }

private class LocalDateProgressionIterator(
    first: LocalDate,
    last: LocalDate,
    step: Int
) : Iterator<LocalDate> {
    private val finalElement = last
    private var hasNext: Boolean = if (step > 0) first <= last else first >= last
    private val step: Long = step.toLong()
    private var next = if (hasNext) first else finalElement

    override fun hasNext(): Boolean = hasNext

    override fun next(): LocalDate {
        val value = next

        if (value == finalElement) {
            if (!hasNext) throw NoSuchElementException()
            hasNext = false
        } else {
            next = next.plusDays(step)
        }

        return value
    }
}

private fun getProgressionLastElement(
    start: LocalDate,
    end: LocalDate,
    step: Int
): LocalDate =
    when {
        step > 0 -> if (start >= end) end else end.minusDays(differenceModulo(start, end, step))
        step < 0 -> if (start <= end) end else end.plusDays(differenceModulo(start, end, -step))
        else -> throw IllegalArgumentException("간격은 0일 수 없습니다.")
    }

// (a - b) mod c
private fun differenceModulo(
    a: LocalDate,
    b: LocalDate,
    c: Int
): Long {
    val aModC = a.toEpochDay() % c
    val bModC = b.toEpochDay() % c
    return if (aModC >= bModC) aModC - bModC else (aModC + c) - bModC
}
