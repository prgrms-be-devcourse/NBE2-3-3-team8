package org.programmers.signalbuddy.domain.member.entity.enums

enum class MemberStatus(val status: String) {
    ACTIVITY("활성"),
    WITHDRAWAL("탈퇴");

    override fun toString(): String = status
}