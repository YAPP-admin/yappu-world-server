package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.dto.SessionAttendeeDto
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AttendanceFindService(
    private val attendanceRepository: AttendanceRepository
) {

    fun hasAlreadyCheckedIn(
        userId: UUID,
        sessionId: UUID
    ): Boolean = attendanceRepository.existsAttendanceByUserIdAndScheduleId(userId, sessionId)

    fun findSessionAttendance(
        userId: UUID,
        sessionId: UUID
    ): AttendanceEntity? = attendanceRepository.findByUserIdAndScheduleId(userId, sessionId)

    fun findAttendancesBySchedules(
        userId: UUID,
        scheduleIds: List<UUID>
    ): List<AttendanceEntity> =
        attendanceRepository
            .findAll {
                select(entity(AttendanceEntity::class))
                    .from(entity(AttendanceEntity::class))
                    .where(
                        and(
                            path(AttendanceEntity::userId).equal(userId),
                            path(AttendanceEntity::scheduleId).`in`(scheduleIds)
                        )
                    )
            }.filterNotNull()

    fun findAttendancesOfGeneration(generation: Int): List<AttendanceEntity> =
        attendanceRepository
            .findAll {
                select(entity(AttendanceEntity::class))
                    .from(
                        entity(AttendanceEntity::class),
                        join(entity(SessionEntity::class))
                            .on(
                                and(
                                    path(SessionEntity::generation).equal(generation),
                                    path(SessionEntity::getId).equal(path(AttendanceEntity::scheduleId))
                                )
                            )
                    )
            }.filterNotNull()

    fun findAttendances(sessionAndUserIds: List<Pair<UUID, UUID>>): List<AttendanceEntity> =
        attendanceRepository
            .findAll {
                val predicates = sessionAndUserIds.map { (sessionId, userId) ->
                    and(
                        path(AttendanceEntity::userId).equal(userId),
                        path(AttendanceEntity::scheduleId).equal(sessionId)
                    )
                }
                select(entity(AttendanceEntity::class))
                    .from(entity(AttendanceEntity::class))
                    .where(or(*predicates.toTypedArray()))
            }.filterNotNull()

    fun findAttendances(sessionId: UUID): List<AttendanceEntity> =
        attendanceRepository
            .findAll {
                select(entity(AttendanceEntity::class))
                    .from(entity(AttendanceEntity::class))
                    .where(path(AttendanceEntity::scheduleId).equal(sessionId))
            }.filterNotNull()

    fun findAttendees(sessionId: UUID): List<SessionAttendeeDto> =
        attendanceRepository
            .findAll {
                selectNew<SessionAttendeeDto>(
                    path(AttendanceEntity::scheduleId),
                    path(AttendanceEntity::userId),
                    path(SessionEntity::generation),
                    path(ActivityUnitEntity::position),
                    path(UserEntity::name)
                ).from(
                    entity(AttendanceEntity::class),
                    join(entity(SessionEntity::class))
                        .on(
                            and(
                                path(SessionEntity::getId).equal(sessionId),
                                path(SessionEntity::getId).equal(path(AttendanceEntity::scheduleId))
                            )
                        ),
                    join(entity(ActivityUnitEntity::class))
                        .on(
                            and(
                                path(ActivityUnitEntity::userId).equal(path(AttendanceEntity::userId)),
                                path(ActivityUnitEntity::generation).equal(path(SessionEntity::generation)),
                                path(ActivityUnitEntity::position).notEqual(Position.STAFF)
                            )
                        ),
                    join(entity(UserEntity::class))
                        .on(path(UserEntity::getId).equal(path(AttendanceEntity::userId)))
                )
            }.filterNotNull()
}
