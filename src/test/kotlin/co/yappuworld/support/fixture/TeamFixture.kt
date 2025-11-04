package co.yappuworld.support.fixture

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.jpa.TeamRepository
import co.yappuworld.team.infrastructure.jpa.TeamServiceRepository

object TeamFixture {
    fun getTeamEntityFixture(
        generation: Int = 35,
        name: String = "테스트팀"
    ): TeamEntity =
        TeamEntity(
            generation = generation,
            name = name
        )

    fun getTeamServiceEntityFixture(
        team: TeamEntity,
        name: String? = null,
        hasApp: Boolean = false,
        hasWeb: Boolean = false,
        googlePlayLink: String? = null,
        appStoreLink: String? = null,
        webLink: String? = null
    ): TeamServiceEntity {
        val serviceLinks = if (googlePlayLink != null || appStoreLink != null || webLink != null) {
            ServiceLinks(
                googlePlay = googlePlayLink,
                appStore = appStoreLink,
                web = webLink
            )
        } else {
            null
        }

        return TeamServiceEntity(
            team = team,
            name = name,
            hasApp = hasApp,
            hasWeb = hasWeb,
            serviceLinks = serviceLinks
        )
    }

    fun getFullServiceLinksFixture(): ServiceLinks =
        ServiceLinks(
            googlePlay = "https://play.google.com/store/apps/details?id=com.yappuworld.test",
            appStore = "https://apps.apple.com/app/id123456789",
            web = "https://test.yappuworld.co"
        )

    fun getEmptyServiceLinksFixture(): ServiceLinks =
        ServiceLinks(
            googlePlay = null,
            appStore = null,
            web = null
        )

    fun TeamRepository.saveTeamWithService(
        serviceRepository: TeamServiceRepository,
        generation: Int = 35,
        name: String = "테스트팀",
        serviceName: String? = null,
        hasApp: Boolean = false,
        hasWeb: Boolean = false,
        googlePlayLink: String? = null,
        appStoreLink: String? = null,
        webLink: String? = null
    ): TeamEntity {
        val team = save(getTeamEntityFixture(generation, name))
        serviceRepository.save(
            getTeamServiceEntityFixture(
                team = team,
                name = serviceName,
                hasApp = hasApp,
                hasWeb = hasWeb,
                googlePlayLink = googlePlayLink,
                appStoreLink = appStoreLink,
                webLink = webLink
            )
        )
        return team
    }

    // ✅ 새로운 헬퍼: 여러 팀을 한 번에 생성
    fun TeamRepository.saveTeamsWithServices(
        serviceRepository: TeamServiceRepository,
        teams: List<TeamData>
    ): List<TeamEntity> =
        saveAll(
            teams.map { data ->
                getTeamEntityFixture(data.generation, data.name)
            }
        ).also { savedTeams ->
            serviceRepository.saveAll(
                savedTeams.zip(teams).map { (team, data) ->
                    getTeamServiceEntityFixture(
                        team = team,
                        name = data.serviceName,
                        hasApp = data.hasApp,
                        hasWeb = data.hasWeb
                    )
                }
            )
        }

    // ✅ 데이터 클래스
    data class TeamData(
        val generation: Int,
        val name: String,
        val serviceName: String? = null,
        val hasApp: Boolean = false,
        val hasWeb: Boolean = false
    )
}
