package org.programmers.signalbuddy.domain.like.dto

enum class LikeRequestType (
    val requestType: String
) {
    ADD("추가 요청"), CANCEL("취소 요청");
}
