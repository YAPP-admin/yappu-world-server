package co.yappuworld.user.infrastructure

import co.yappuworld.user.infrastructure.jpa.SignUpApplicationEntity

interface UserSystemNotifier {

    fun notifySignUpRequestReceived(signUpApplication: SignUpApplicationEntity)
}
