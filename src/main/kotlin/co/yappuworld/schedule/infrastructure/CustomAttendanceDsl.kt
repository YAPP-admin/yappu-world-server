package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.dto.SessionWithAttendanceDto
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryFromStep
import org.springframework.stereotype.Component

@Component
class CustomAttendanceDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomAttendanceDsl> {
        override fun newInstance(): CustomAttendanceDsl = CustomAttendanceDsl()
    }

    fun selectSessionWithAttendance(): SelectQueryFromStep<SessionWithAttendanceDto> =
        selectNew<SessionWithAttendanceDto>(
            path(SessionEntity::getId),
            path(SessionEntity::name),
            path(SessionEntity::description),
            path(SessionEntity::place),
            path(SessionEntity::address),
            path(SessionEntity::latitude),
            path(SessionEntity::longitude),
            path(SessionEntity::date),
            path(SessionEntity::endDate),
            path(SessionEntity::time),
            path(SessionEntity::endTime),
            path(SessionEntity::generation),
            path(SessionEntity::sessionType),
            path(AttendanceEntity::createdAt),
            path(AttendanceEntity::status)
        )
}
