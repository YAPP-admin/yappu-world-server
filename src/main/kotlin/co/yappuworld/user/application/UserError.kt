package co.yappuworld.user.application

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class UserError : Error {
    INVALID_SIGN_UP_CODE {
        override val message: String = "잘못된 가입코드입니다."
        override val code: String = "USR-0001"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    },
    ALREADY_SIGNED_UP_EMAIL {
        override val message: String = "이미 가입된 이메일입니다."
        override val code: String = "USR-0002"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    UNPROCESSED_APPLICATION_EXISTS {
        override val message: String = "처리되지 않은 가입 신청이 존재하여, 추가 가입 신청이 불가합니다."
        override val code: String = "USR-0003"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },

    // 2000번대 - 회원가입 에러
    NOT_FOUND_SIGN_UP_APPLICATION {
        override val message: String = "회원가입 신청 내역을 찾을 수 없습니다."
        override val code: String = "USR-2001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },

    // 2100번대 - 로그인 에러
    FAIL_LOGIN_NOT_FOUND_USER {
        override val message: String = "계정 정보를 찾을 수 없습니다."
        override val code: String = "USR-2101"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    CANNOT_LOGIN_WITH_UNPROCESSED_SIGN_UP_APPLICATION {
        override val message: String = "회원가입 처리가 진행 중입니다."
        override val code: String = "USR-2102"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    RECENT_SIGN_UP_APPLICATION_REJECTED {
        override val message: String = "최근의 회원가입 신청은 거절되었습니다."
        override val code: String = "USR-2103"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    CANNOT_LOGIN_WRONG_USER_STATE {
        override val message: String = "로그인이 불가능한 회원 상태입니다."
        override val code: String = "USR-2104"
        override val type: ErrorType = ErrorType.WRONG_STATE
    }
}
