package co.yappuworld.support.fixture

import co.yappuworld.user.domain.User
import co.yappuworld.user.domain.UserRole

fun getUserFixture(
    email: String = "email@email.com",
    password: String = "password",
    name: String = "name",
    role: UserRole = UserRole.ACTIVE,
    isActive: Boolean = true
) = User(
    email = email,
    password = password,
    name = name,
    role = role,
    isActive = isActive
)
