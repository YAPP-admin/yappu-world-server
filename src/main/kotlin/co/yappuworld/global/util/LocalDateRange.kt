package co.yappuworld.global.util

import java.time.LocalDate

class LocalDateRange(
    val start: LocalDate,
    val endInclusive: LocalDate
) : Iterable<LocalDate> {

    override fun iterator(): Iterator<LocalDate> {
        return object : Iterator<LocalDate> {
            private var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): LocalDate {
                if (!hasNext()) throw NoSuchElementException()
                return current.also { current = current.plusDays(1) }
            }
        }
    }
}
