package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.UserSignUpApplicationStatus
import co.yappuworld.user.domain.SignUpApplication
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSignUpApplicationRepository : CrudRepository<SignUpApplication, UUID> {

    fun findByApplicantEmailAndStatus(
        applicantEmail: String,
        status: UserSignUpApplicationStatus
    ): List<SignUpApplication>
}
