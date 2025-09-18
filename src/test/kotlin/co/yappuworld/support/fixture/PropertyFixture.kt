package co.yappuworld.support.fixture

import co.yappuworld.global.security.JwtProperties

object PropertyFixture {

    fun getJwtProperty(
        secretKey: String = "thisisforlocalsecretkeyonlyusinginlocalenvironmentthisisforlocalsecretkeyonlyusinginlocal",
        accessTokenExpirationTimes: Int = 3600000,
        refreshTokenExpirationTimes: Int = 1209600000
    ): JwtProperties =
        JwtProperties(
            secretKey,
            accessTokenExpirationTimes,
            refreshTokenExpirationTimes
        )
}
