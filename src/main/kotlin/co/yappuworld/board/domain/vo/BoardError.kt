package co.yappuworld.board.domain.vo

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class BoardError : Error {
    BOARD_NOT_FOUND {
        override val message: String = "게시글이 존재하지 않습니다."
        override val code: String = "BRD_0001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    }
}
