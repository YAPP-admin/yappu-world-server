package co.yappuworld.schedule.infrastructure.jdsl

import co.yappuworld.schedule.domain.SessionParticipant
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionParticipantEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.UserEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryWhereStep
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sortable
import org.springframework.stereotype.Component

@Component
class CustomSessionDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomSessionDsl> {
        override fun newInstance(): CustomSessionDsl = CustomSessionDsl()
    }

    fun sessionSorting(): List<Sortable> =
        listOf(
            path(SessionEntity::date).asc(),
            path(SessionEntity::time).asc(),
            path(SessionEntity::endDate).asc(),
            path(SessionEntity::endTime).asc()
        )

    fun selectFromSessionParticipant(): SelectQueryWhereStep<SessionParticipant> =
        selectNew<SessionParticipant>(
            path(ActivityUnitEntity::getId),
            path(ActivityUnitEntity::generation),
            path(ActivityUnitEntity::position),
            path(UserEntity::getId),
            path(UserEntity::name),
            path(SessionEntity::getId),
            path(SessionEntity::name),
            path(SessionParticipantEntity::attendanceStatus)
        ).from(
            entity(SessionParticipantEntity::class),
            join(SessionParticipantEntity::activityUnit),
            join(SessionParticipantEntity::session),
            join(UserEntity::class).on(
                path(SessionParticipantEntity::activityUnit)(ActivityUnitEntity::userId)
                    .equal(path(UserEntity::getId))
            )
        )
}
