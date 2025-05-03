package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity

interface UserSystemNotifier {

    fun notifySignUpRequestReceived(signUpApplication: SignUpApplicationEntity)
}
