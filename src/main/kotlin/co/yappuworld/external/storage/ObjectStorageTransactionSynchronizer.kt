package co.yappuworld.external.storage

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class ObjectStorageTransactionSynchronizer(
    private val objectStorageManager: ObjectStorageManager
) {
    fun registerDeleteOnRollback(objectKey: String) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return
        }

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCompletion(status: Int) {
                    if (status != TransactionSynchronization.STATUS_COMMITTED) {
                        deleteObject(objectKey)
                    }
                }
            }
        )
    }

    fun registerDeleteAfterCommit(objectKey: String) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteObject(objectKey)
            return
        }

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    deleteObject(objectKey)
                }
            }
        )
    }

    private fun deleteObject(objectKey: String) {
        runCatching {
            objectStorageManager.delete(objectKey)
        }.onFailure {
            log.warn("이미지 파일 삭제에 실패했습니다. objectKey={}", objectKey, it)
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(ObjectStorageTransactionSynchronizer::class.java)
    }
}
