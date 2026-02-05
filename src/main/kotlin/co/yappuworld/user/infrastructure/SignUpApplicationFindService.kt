package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.client.dto.request.AdminSignUpApplicationPageRequest
import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.entity.SignUpApplicationActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SignUpApplicationFindService(
    private val signUpApplicationRepository: SignUpApplicationRepository
) {

    fun existsPendingApplication(email: String): Boolean =
        signUpApplicationRepository
            .findAll(limit = 1) {
                select(entity(SignUpApplicationEntity::class))
                    .from(entity(SignUpApplicationEntity::class))
                    .where(
                        and(
                            path(SignUpApplicationEntity::applicantEmail).equal(email),
                            path(SignUpApplicationEntity::status).equal(SignUpApplicationStatus.PENDING)
                        )
                    ).orderBy(path(SignUpApplicationEntity::createdAt).desc())
            }.isNotEmpty()

    fun findPendingApplicationOrNull(email: String): SignUpApplicationEntity? =
        signUpApplicationRepository
            .findAll(limit = 1) {
                select(entity(SignUpApplicationEntity::class))
                    .from(entity(SignUpApplicationEntity::class))
                    .where(
                        and(
                            path(SignUpApplicationEntity::applicantEmail).equal(email),
                            path(SignUpApplicationEntity::status).equal(SignUpApplicationStatus.PENDING)
                        )
                    ).orderBy(path(SignUpApplicationEntity::createdAt).desc())
            }.singleOrNull()

    fun findSignUpApplications(ids: List<UUID>): List<SignUpApplicationEntity> {
        require(ids.isNotEmpty()) { "가입 신청서 조회 대상 ID 목록은 최소 하나 이상이어야 합니다." }
        return signUpApplicationRepository.findAllByIdIn(ids)
    }

    fun findLatestSignUpApplication(email: String): SignUpApplicationEntity? =
        signUpApplicationRepository
            .findAll(limit = 1) {
                select(entity(SignUpApplicationEntity::class))
                    .from(entity(SignUpApplicationEntity::class))
                    .where(path(SignUpApplicationEntity::applicantEmail).equal(email))
                    .orderBy(path(SignUpApplicationEntity::createdAt).desc())
            }.firstOrNull()

    fun findSignUpApplication(applicationId: UUID): SignUpApplicationEntity =
        signUpApplicationRepository.findByIdOrNull(applicationId)
            ?: throw BusinessException(UserError.NOT_FOUND_SIGN_UP_APPLICATION)

    fun findSignUpApplications(
        request: AdminSignUpApplicationPageRequest,
        pageRequest: PageRequest
    ): Page<SignUpApplicationEntity> {
        val hasActivityUnitFilter = request.generation != null || request.position != null

        val applications = signUpApplicationRepository
            .findAll {
                select(entity(SignUpApplicationEntity::class))
                    .from(
                        entity(SignUpApplicationEntity::class),
                        *listOfNotNull(
                            hasActivityUnitFilter.takeIf { it }?.let {
                                leftJoin(SignUpApplicationActivityUnitEntity::class).on(
                                    path(SignUpApplicationActivityUnitEntity::applicationId)
                                        .equal(path(SignUpApplicationEntity::getId))
                                )
                            }
                        ).toTypedArray()
                    ).whereAnd(
                        *buildList {
                            request.status?.let { add(path(SignUpApplicationEntity::status).equal(it)) }
                            request.name?.takeIf { it.isNotBlank() }?.let {
                                add(
                                    path(SignUpApplicationEntity::applicantName).like("%$it%")
                                )
                            }
                            when {
                                hasActivityUnitFilter -> {
                                    request.generation?.let {
                                        add(
                                            path(SignUpApplicationActivityUnitEntity::generation).equal(it)
                                        )
                                    }
                                    request.position?.let {
                                        add(
                                            path(SignUpApplicationActivityUnitEntity::position).equal(it)
                                        )
                                    }
                                }
                            }
                        }.toTypedArray()
                    ).orderBy(path(SignUpApplicationEntity::createdAt).desc())
            }.distinct()

        return PageImpl(
            applications.drop(pageRequest.offset.toInt()).take(pageRequest.pageSize),
            pageRequest,
            applications.size.toLong()
        )
    }
}
