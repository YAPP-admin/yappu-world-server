package co.yappuworld.post.domain

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class PostError : Error {
    POST_NOT_FOUND {
        override val message: String = "게시글이 존재하지 않습니다."
        override val code: String = "BRD_0001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },

    // 공지사항 1xxx
    NOTICE_NOT_FOUND {
        override val message: String = "공지사항이 존재하지 않습니다."
        override val code: String = "BRD_1000"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    NOTICE_WRITER_NOT_FOUND {
        override val message: String = "공지사항 작성자 정보가 존재하지 않습니다."
        override val code: String = "BRD_1001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    NOT_CONTAIN_DELETE_NOTICE {
        override val message: String = "존재하지 않는 공지사항이 요청에 포함되어 있습니다."
        override val code: String = "PST_1002"
        override val type: ErrorType = ErrorType.NOT_FOUND
    }
}
