package co.yappuworld.support.environment

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

@CustomDataJpaTest
abstract class CustomDataJpaTestFeatureSpec(
    body: FeatureSpec.() -> Unit
) : FeatureSpec(body) {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Test))
}
