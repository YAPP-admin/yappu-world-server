package co.yappuworld.user.domain.vo

import co.yappuworld.global.exception.Error
import co.yappuworld.global.exception.ErrorType

enum class UserError : Error {

    // 회원 일반 에러
    USER_NOT_FOUND {
        override val message: String = "유저가 존재하지 않습니다."
        override val code: String = "USR_0001"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    WITHDRAWN_USER {
        override val message: String = "탈퇴한 유저입니다."
        override val code: String = "USR_0002"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    USER_RELATED_DATA_NOT_FOUND {
        override val message: String = "유저 데이터를 확인해주세요."
        override val code: String = "USR_0003"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    ACTIVE_UNIT_IS_ESSENTIAL {
        override val message: String = "활동 정보는 하나 이상 존재해야 합니다."
        override val code: String = "USR_0004"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    USER_FIND_ERROR {
        override val message: String = "유저 조회에 에러가 발생했습니다."
        override val code: String = "USR_0005"
        override val type: ErrorType = ErrorType.UNEXPECTED_ERROR
    },
    USER_NOT_FOUND_WITH_GENERATION_ACTIVITY {
        override val message: String = "해당 세대의 활동 정보를 가진 유저를 찾을 수 없습니다."
        override val code: String = "USR_0006"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },

    // 1000번대 - 회원가입 에러
    WRONG_SIGN_UP_CODE {
        override val message: String = "잘못된 가입코드입니다."
        override val code: String = "USR_1001"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    },
    ALREADY_SIGNED_UP_EMAIL {
        override val message: String = "이미 가입된 이메일입니다."
        override val code: String = "USR_1002"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    UNPROCESSED_APPLICATION_EXISTS {
        override val message: String = "처리되지 않은 가입 신청이 존재하여, 추가 가입 신청이 불가합니다."
        override val code: String = "USR_1003"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    CONTAIN_NOT_EXIST_APPLICATION_ID {
        override val message: String = "존재하지 않는 가입 신청 ID가 포함되어 있습니다."
        override val code: String = "USR_1004"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    CONTAIN_ALREADY_PROCESSED_APPLICATION {
        override val message: String = "이미 처리된 가입 신청이 포함되어 있습니다."
        override val code: String = "USR_1005"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    },
    ALREADY_PROCESSED_EMAIL {
        override val message: String = "이미 처리 중인 이메일입니다."
        override val code: String = "USR_1098"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    NOT_FOUND_SIGN_UP_APPLICATION {
        override val message: String = "회원가입 신청 내역을 찾을 수 없습니다."
        override val code: String = "USR_1099"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },

    // 1100번대 - 로그인 에러
    FAIL_LOGIN_NOT_FOUND_USER {
        override val message: String = "계정 정보를 찾을 수 없습니다."
        override val code: String = "USR_1101"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    CANNOT_LOGIN_WITH_UNPROCESSED_SIGN_UP_APPLICATION {
        override val message: String = "회원가입 처리가 진행 중입니다."
        override val code: String = "USR_1102"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    RECENT_SIGN_UP_APPLICATION_REJECTED {
        override val message: String = "최근의 회원가입 신청은 거절되었습니다."
        override val code: String = "USR_1103"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    CANNOT_LOGIN_WRONG_USER_STATE {
        override val message: String = "로그인이 불가능한 회원 상태입니다."
        override val code: String = "USR_1104"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    WRONG_LOGIN_USER_INFORMATION {
        override val message: String = "로그인에 실패했습니다. 계정 정보를 다시 확인하세요."
        override val code: String = "USR_1105"
        override val type: ErrorType = ErrorType.UNAUTHORIZED
    },
    MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION {
        override val message: String = "가입 시 입력한 계정 정보와 일치하지 않습니다."
        override val code: String = "USR_1121"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    NO_SIGN_UP_APPLICATION {
        override val message: String = "회원가입 신청을 한 내역이 없습니다."
        override val code: String = "USR_1122"
        override val type: ErrorType = ErrorType.NOT_FOUND
    },
    NO_AUTH_FOR_ADMIN_PAGE {
        override val message: String = "어드민 페이지에 로그인할 권한이 없습니다."
        override val code: String = "USR_1190"
        override val type: ErrorType = ErrorType.FORBIDDEN
    },

    // 1200번대 - 회원탈퇴 에러
    ALREADY_WITHDRAWN_USER {
        override val message: String = "이미 탈퇴한 계정입니다."
        override val code: String = "USR_1201"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },

    // 2000번대 - 가입 코드
    SIGN_UP_CODE_UNREGISTERED {
        override val message: String = "가입코드가 등록되지 않았습니다."
        override val code: String = "USR_2000"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    INVALID_SIGN_UP_CODE {
        override val message: String = "가입코드가 형식에 맞지 않습니다."
        override val code: String = "USR_2001"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    SIGN_UP_CODE_DUPLICATED {
        override val message: String = "가입코드가 중복되었습니다."
        override val code: String = "USR_2002"
        override val type: ErrorType = ErrorType.WRONG_STATE
    },
    CANNOT_UPDATE_EXISTS_SIGN_UP_CODE {
        override val message: String = "기존에 존재하는 가입코드이므로 변경할 수 없습니다."
        override val code: String = "USR_2003"
        override val type: ErrorType = ErrorType.WRONG_ARGUMENT
    },

    // 3000번대 - 유저 관리
    DUPLICATE_ACTIVITY_UNIT {
        override val message: String = "기수와 직군이 모두 중복된 활동 정보가 존재합니다."
        override val code: String = "USR_3000"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    },
    WRONG_PHONE_NUMBER {
        override val message: String = "잘못된 전화번호 형식입니다."
        override val code: String = "USR_3001"
        override val type: ErrorType = ErrorType.BAD_REQUEST
    }
}
