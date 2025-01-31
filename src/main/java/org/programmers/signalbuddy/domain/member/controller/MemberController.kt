package org.programmers.signalbuddy.domain.member.controller

import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(
    private val memberService: MemberService
) {

    /**
     * 특정 회원 조회
     */
    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<Member> {
        val member = memberService.getMemberById(id)
        return ResponseEntity.ok(member)
    }
}