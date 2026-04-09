package co.yappuworld.team.client.application

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.TeamFixture.getEmptyServiceLinksFixture
import co.yappuworld.support.fixture.TeamFixture.getFullServiceLinksFixture
import co.yappuworld.support.fixture.TeamFixture.getTeamEntityFixture
import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class ServiceLinksConverterTest @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val serviceRepository: TeamServiceRepository
) : CustomDataJpaTestFeatureSpec({

        val converter = ServiceLinksConverter()

        feature("데이터베이스 통합 테스트") {

            scenario("서비스 링크 null 저장") {
                val team = teamRepository.save(
                    getTeamEntityFixture(generation = 35, name = "테스트팀")
                )

                val service = TeamServiceEntity(
                    team = team,
                    name = "테스트 서비스",
                    hasApp = true,
                    hasWeb = false,
                    serviceLinks = null
                )
                serviceRepository.save(service)

                val found = serviceRepository.findById(service.id).get()
                found.serviceLinks.shouldBeNull()
            }

            scenario("일부 링크만 있는 ServiceLinks") {
                val team = teamRepository.save(
                    getTeamEntityFixture(generation = 35, name = "테스트팀")
                )

                val partialLinks = ServiceLinks(
                    googlePlay = "https://play.google.com/store/apps/details?id=com.test",
                    appStore = null,
                    web = "https://test.com"
                )
                val service = TeamServiceEntity(
                    team = team,
                    name = "테스트 서비스",
                    hasApp = true,
                    hasWeb = true,
                    serviceLinks = partialLinks
                )
                serviceRepository.save(service)

                val found = serviceRepository.findById(service.id).get()
                found.serviceLinks.shouldNotBeNull()
                found.serviceLinks?.googlePlay shouldBe "https://play.google.com/store/apps/details?id=com.test"
                found.serviceLinks?.appStore.shouldBeNull()
                found.serviceLinks?.web shouldBe "https://test.com"
            }

            scenario("모든 링크가 있는 ServiceLinks") {
                val team = teamRepository.save(
                    getTeamEntityFixture(generation = 35, name = "테스트팀")
                )

                val fullLinks = getFullServiceLinksFixture()
                val service = TeamServiceEntity(
                    team = team,
                    name = "테스트 서비스",
                    hasApp = true,
                    hasWeb = true,
                    serviceLinks = fullLinks
                )
                serviceRepository.save(service)

                val found = serviceRepository.findById(service.id).get()
                found.serviceLinks.shouldNotBeNull()
                found.serviceLinks?.googlePlay shouldBe
                    "https://play.google.com/store/apps/details?id=com.yappuworld.test"
                found.serviceLinks?.appStore shouldBe "https://apps.apple.com/app/id123456789"
                found.serviceLinks?.web shouldBe "https://test.yappuworld.co"
            }

            scenario("null과 빈 객체가 구분된다") {
                val team = teamRepository.save(
                    getTeamEntityFixture(generation = 35, name = "테스트팀")
                )

                val serviceWithNull = TeamServiceEntity(
                    team = team,
                    name = "null 서비스",
                    hasApp = true,
                    hasWeb = false,
                    serviceLinks = null
                )
                serviceRepository.save(serviceWithNull)

                val serviceWithEmpty = TeamServiceEntity(
                    team = team,
                    name = "빈 객체 서비스",
                    hasApp = true,
                    hasWeb = false,
                    serviceLinks = getEmptyServiceLinksFixture()
                )
                serviceRepository.save(serviceWithEmpty)

                val foundNull = serviceRepository.findById(serviceWithNull.id).get()
                foundNull.serviceLinks.shouldBeNull()

                val foundEmpty = serviceRepository.findById(serviceWithEmpty.id).get()
                foundEmpty.serviceLinks.shouldNotBeNull()
            }

            scenario("update 메서드로 null을 설정할 수 있다") {
                val team = teamRepository.save(
                    getTeamEntityFixture(generation = 35, name = "테스트팀")
                )

                val service = TeamServiceEntity(
                    team = team,
                    name = "테스트 서비스",
                    hasApp = true,
                    hasWeb = true,
                    serviceLinks = getFullServiceLinksFixture()
                )
                serviceRepository.save(service)

                service.update(
                    team = team,
                    name = "업데이트된 서비스",
                    hasApp = true,
                    hasWeb = false,
                    serviceLinks = null,
                    summary = null,
                    description = null,
                    isOperating = false
                )

                serviceRepository.save(service)

                val found = serviceRepository.findById(service.id).get()
                found.serviceLinks.shouldBeNull()
            }
        }

        feature("Converter 단위 테스트") {

            scenario("null 입력 시 null 반환") {
                val result = converter.convertToDatabaseColumn(null)
                result.shouldBeNull()
            }

            scenario("ServiceLinks -> JSON 직렬화") {
                val links = ServiceLinks(
                    googlePlay = "https://play.google.com/test",
                    appStore = null,
                    web = "https://test.com"
                )
                val result = converter.convertToDatabaseColumn(links)

                result.shouldNotBeNull()
                result shouldBe
                    """{"googlePlay":"https://play.google.com/test","appStore":null,"web":"https://test.com"}"""
            }

            scenario("JSON -> ServiceLinks 역직렬화") {
                val json = """{"googlePlay":"https://play.google.com/test","appStore":null,"web":"https://test.com"}"""
                val result = converter.convertToEntityAttribute(json)

                result.shouldNotBeNull()
                result.googlePlay shouldBe "https://play.google.com/test"
                result.appStore.shouldBeNull()
                result.web shouldBe "https://test.com"
            }
        }
    })
