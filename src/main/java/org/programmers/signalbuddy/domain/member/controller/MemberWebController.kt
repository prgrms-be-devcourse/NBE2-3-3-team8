package org.programmers.signalbuddy.domain.member.controller

import lombok.extern.slf4j.Slf4j
import org.programmers.signalbuddy.domain.member.service.MemberService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Slf4j
@Controller
@RequestMapping("members")
class MemberWebController(
    private val memberService: MemberService
) {
    @ModelAttribute("user")
    fun currentUser(@CurrentUser user: CustomUser2Member): CustomUser2Member? {
        return user
    }

    @GetMapping("/login")
    fun loginForm(@RequestParam(required = false) error: String?, mv: ModelAndView): ModelAndView {
        if (error != null) {
            mv.addObject("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.")
        }
        mv.setViewName("member/loginform")
        return mv
    }
}

