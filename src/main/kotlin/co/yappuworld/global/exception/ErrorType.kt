package co.yappuworld.global.exception

/**
 * @property WRONG_ARGUMENT 잘못된 파라미터, 요청값
 * @property UNAUTHORIZED 인증, 인가 오류
 * @property NOT_FOUND 리소스를 찾지 못하는 모든 상황에 해당
 * @property WRONG_STATE 요청을 처리할 수 없는 상태 (데이터 정합성 문제, 비즈니스 요구사항 문제)
 * @property UNEXPECTED_ERROR 예기치 못한 에러의 발생 (Internal Server Error)
 */
enum class ErrorType {
    WRONG_ARGUMENT,
    UNAUTHORIZED,
    NOT_FOUND,
    WRONG_STATE,
    UNEXPECTED_ERROR
}
