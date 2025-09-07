package co.yappuworld.global.persistence

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@NoRepositoryBean
interface CustomJdslJpqlExecutor {
    fun <T : Any> singleOrNull(init: Jpql.() -> JpqlQueryable<SelectQuery<T>>): T?

    fun <T : Any, DSL : JpqlDsl> singleOrNull(
        dsl: JpqlDsl.Constructor<DSL>,
        init: DSL.() -> JpqlQueryable<SelectQuery<T>>
    ): T?

    fun <T : Any, DSL : JpqlDsl> singleOrNull(
        jpql: DSL,
        init: DSL.() -> JpqlQueryable<SelectQuery<T>>
    ): T?
}

@Component
@NoRepositoryBean
@Transactional(readOnly = true)
class CustomJdslJpqlExecutorImpl(
    val entityManager: EntityManager,
    val context: JpqlRenderContext
) : CustomJdslJpqlExecutor {

    override fun <T : Any> singleOrNull(init: Jpql.() -> JpqlQueryable<SelectQuery<T>>): T? = singleOrNull(Jpql, init)

    override fun <T : Any, DSL : JpqlDsl> singleOrNull(
        dsl: JpqlDsl.Constructor<DSL>,
        init: DSL.() -> JpqlQueryable<SelectQuery<T>>
    ): T? = singleOrNull(dsl.newInstance(), init)

    override fun <T : Any, DSL : JpqlDsl> singleOrNull(
        jpql: DSL,
        init: DSL.() -> JpqlQueryable<SelectQuery<T>>
    ): T? {
        val query: SelectQuery<T> = jpql.init().toQuery()
        return try {
            entityManager.createQuery(query, context).singleResult
        } catch (e1: NoResultException) {
            return null
        } catch (e2: NonUniqueResultException) {
            return null
        } catch (e3: Exception) {
            throw e3
        }
    }
}
