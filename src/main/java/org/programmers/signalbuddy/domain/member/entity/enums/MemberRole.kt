package org.programmers.signalbuddy.domain.member.entity.enums

enum class MemberRole(val role: String) {
    ADMIN("관리자"),
    USER("사용자");

    override fun toString(): String = role
}