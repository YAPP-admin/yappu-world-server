package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.entity.SignUpApplicationEntity

interface UserSystemNotifier {

    fun notifySignUpRequestReceived(signUpApplication: SignUpApplicationEntity)
}
