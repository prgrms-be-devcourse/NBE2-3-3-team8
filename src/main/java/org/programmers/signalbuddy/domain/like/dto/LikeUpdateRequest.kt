package org.programmers.signalbuddy.domain.like.dto

import org.programmers.signalbuddy.domain.like.exception.LikeErrorCode
import org.programmers.signalbuddy.global.exception.BusinessException

class LikeUpdateRequest (
    val feedbackId: Long,
    val memberId: Long,
    likeRequestType: String
) {

    val likeRequestType: LikeRequestType = toEnum(likeRequestType)

    private fun toEnum(likeRequestType: String): LikeRequestType {
        if (likeRequestType == LikeRequestType.ADD.name) {
            return LikeRequestType.ADD
        }
        if (likeRequestType == LikeRequestType.CANCEL.name) {
            return LikeRequestType.CANCEL
        }

        throw BusinessException(LikeErrorCode.ILLEGAL_REQUEST_TYPE)
    }
}
