package org.programmers.signalbuddy.domain.comment.exception

import org.programmers.signalbuddy.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class CommentErrorCode(
    override val httpStatus: HttpStatus,
    override val code: Int,
    override val message: String,
) : ErrorCode {

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, 20000, "해당 댓글은 찾을 수 없습니다."),
    COMMENT_MODIFIER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, 20001, "해당 댓글의 작성자와 일치하지 않습니다."),
    COMMENT_ELIMINATOR_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, 20002, "해당 댓글의 작성자와 일치하지 않습니다.");
}
