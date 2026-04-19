package co.yappuworld.support.environment

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
    properties = [
        "app.lock.signup-email.strategy=fake"
    ]
)
@Transactional
abstract class SpringBootTestFeatureSpec(
    body: FeatureSpec.() -> Unit
) : FeatureSpec(body) {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Test))
}
