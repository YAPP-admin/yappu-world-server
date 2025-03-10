package co.yappuworld.support.fixture.schedule

import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.infrastructure.entity.SessionJdbcEntity
import java.time.LocalDate
import java.time.LocalTime

object ScheduleFixture {

    fun getSessionJdbcEntityFixture(
        name: String = "시범 세션",
        description: String? = "시범 세션이니깐 걱정 마세요",
        place: String? = "공덕 창업 허브",
        date: LocalDate = LocalDate.of(2025, 2, 15),
        endDate: LocalDate? = null,
        time: LocalTime = LocalTime.of(14, 0, 0),
        endTime: LocalTime? = null,
        generation: Int = 25
    ) = SessionJdbcEntity(
        name = name,
        description = description,
        place = place,
        date = date,
        endDate = endDate,
        time = time,
        endTime = endTime,
        generation = generation
    )

    fun getSessionFixture(
        name: String = "시범 세션",
        description: String? = "시범 세션이니깐 걱정 마세요",
        place: String? = "공덕 창업 허브",
        date: LocalDate = LocalDate.of(2025, 2, 15),
        endDate: LocalDate? = null,
        time: LocalTime = LocalTime.of(14, 0, 0),
        endTime: LocalTime? = null,
        generation: Int = 25
    ) = SessionEntity(
        name = name,
        description = description,
        place = place,
        date = date,
        endDate = endDate,
        time = time,
        endTime = endTime,
        generation = generation
    )
}
