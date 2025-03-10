package co.yappuworld.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["co.yappuworld.**.infrastructure"])
@EnableJpaAuditing
class JpaConfig
