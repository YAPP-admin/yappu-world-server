package co.yappuworld.schedule.infrastructure

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SessionParticipantFindService
