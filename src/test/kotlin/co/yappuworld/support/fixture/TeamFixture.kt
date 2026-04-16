package co.yappuworld.support.fixture

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity

object TeamFixture {
    fun getTeamEntityFixture(
        generation: Int = 35,
        name: String = "테스트팀",
        hasApp: Boolean = false,
        hasWeb: Boolean = true
    ): TeamEntity =
        TeamEntity(
            generation = generation,
            name = name,
            hasApp = hasApp,
            hasWeb = hasWeb
        )

    fun getTeamServiceEntityFixture(
        team: TeamEntity,
        name: String? = null,
        hasApp: Boolean = false,
        hasWeb: Boolean = false,
        googlePlayLink: String? = null,
        appStoreLink: String? = null,
        webLink: String? = null,
        summary: String? = null,
        description: String? = null,
        isOperating: Boolean = false
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
            serviceLinks = serviceLinks,
            summary = summary,
            description = description,
            isOperating = isOperating
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
}
