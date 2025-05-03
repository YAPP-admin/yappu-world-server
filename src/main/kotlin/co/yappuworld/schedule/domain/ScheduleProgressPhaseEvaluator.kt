package co.yappuworld.schedule.domain

import co.yappuworld.schedule.domain.ScheduleProgressPhase.DONE
import co.yappuworld.schedule.domain.ScheduleProgressPhase.ONGOING
import co.yappuworld.schedule.domain.ScheduleProgressPhase.PENDING
import co.yappuworld.schedule.infrastructure.entity.ScheduleEntity
import java.time.LocalDateTime

fun ScheduleEntity.getProgressPhase(now: LocalDateTime): ScheduleProgressPhase =
    when {
        // 이미 시작했음
        date.isBefore(now.toLocalDate()) -> decideProgressPhaseAlreadyStarted(now)
        // 오늘 일정인데, 상태는 모름
        date.isEqual(now.toLocalDate()) -> decideProgressPhaseForStartingToday(now)
        // 시작하려면 멀었음
        else -> PENDING
    }

private fun ScheduleEntity.decideProgressPhaseForStartingToday(now: LocalDateTime): ScheduleProgressPhase =
    when {
        // 시작함
        time.isBefore(now.toLocalTime()) -> when {
            // 종료 시간 없으므로 끝난 거
            endTime.isBefore(now.toLocalTime()) -> DONE
            else -> ONGOING
        }
        // 시작 안 함
        else -> PENDING
    }

private fun ScheduleEntity.decideProgressPhaseAlreadyStarted(now: LocalDateTime): ScheduleProgressPhase =
    when {
        endDate.isBefore(now.toLocalDate()) -> DONE
        endDate.isAfter(now.toLocalDate()) -> ONGOING
        else -> when {
            endTime.isBefore(now.toLocalTime()) -> DONE
            else -> ONGOING
        }
    }
