package co.yappuworld.support.fixture.property

import co.yappuworld.global.property.JwtProperty

object PropertyFixture {

    fun getJwtProperty(
        secretKey: String = "thisisforlocalsecretkeyonlyusinginlocalenvironmentthisisforlocalsecretkeyonlyusinginlocal",
        accessTokenExpirationTimes: Int = 3600000,
        refreshTokenExpirationTimes: Int = 1209600000
    ): JwtProperty {
        return JwtProperty(
            secretKey,
            accessTokenExpirationTimes,
            refreshTokenExpirationTimes
        )
    }
}
