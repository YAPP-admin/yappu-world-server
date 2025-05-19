package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
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
}
