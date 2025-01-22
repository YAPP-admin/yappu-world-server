package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.UserSignUpApplicationStatus
import org.springframework.data.domain.Limit
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSignUpApplicationRepository : CrudRepository<SignUpApplication, UUID> {

    fun findByApplicantEmailAndStatus(
        applicantEmail: String,
        status: UserSignUpApplicationStatus
    ): List<SignUpApplication>

    fun findByApplicantEmailOrderByUpdatedAtDesc(
        applicantEmail: String,
        limit: Limit
    ): SignUpApplication?
}
