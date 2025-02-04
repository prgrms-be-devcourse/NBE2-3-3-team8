package org.programmers.signalbuddy.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.programmers.signalbuddy.domain.bookmark.service.BookmarkService
import org.programmers.signalbuddy.domain.member.dto.MemberJoinRequest
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.feedback.service.FeedbackService
import org.programmers.signalbuddy.domain.member.service.MemberService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Slf4j
@Controller
@RequestMapping("members")
class MemberWebController(
    private val memberService: MemberService,
    private val bookmarkService: BookmarkService,
    private val feedbackService: FeedbackService
) {
    @ModelAttribute("user")
    fun currentUser(@CurrentUser user: CustomUser2Member) = user

    @GetMapping("/login")
    fun loginForm(@RequestParam(required = false) error: String?, mv: ModelAndView): ModelAndView {
        if (error != null) {
            mv.addObject("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.")
        }
        mv.setViewName("member/loginform")
        return mv
    }

    @GetMapping
    fun getMemberView(mv: ModelAndView): ModelAndView {
        mv.viewName = "member/info"
        return mv
    }

    @GetMapping("edit")
    fun editMemberView(mv: ModelAndView): ModelAndView {
        mv.viewName = "member/edit"
        return mv
    }

    @GetMapping("{id}/bookmarks")
    fun findPagedBookmarks(
        @PageableDefault(page = 0, size = 5) pageable: Pageable,
        mv: ModelAndView, @PathVariable id: Long
    ): ModelAndView {
        val pagedBookmarks = bookmarkService.findPagedBookmarks(pageable, id)
        mv.addObject("pagination", pagedBookmarks)
        mv.viewName = "/member/bookmark/list"
        return mv
    }

    @GetMapping("{id}/feedbacks")
    fun findPagedFeedbacks(
        @PageableDefault(page = 0, size = 5) pageable: Pageable,
        mv: ModelAndView, @PathVariable id: Long
    ): ModelAndView {
        val pagedFeedbacks = feedbackService.findPagedFeedbacksByMember(id, pageable)
        mv.addObject("pagination", pagedFeedbacks)
        mv.viewName = "member/feedback/list"
        return mv
    }

    @PostMapping("verify-password")
    @Operation(summary = "현재 사용자의 비밀번호 검사", description = "회원정보 수정 시 비밀번호 확인 API")
    @ApiResponse(responseCode = "200", description = "비밀번호 검사")
    fun verifyPassword(
        @RequestParam password: String,
        @CurrentUser user: CustomUser2Member,
        mv: ModelAndView
    ): ModelAndView {
        val verified: Boolean = memberService.verifyPassword(password, user)
        if (verified) {
            mv.viewName = "redirect:/members/edit"
        } else {
            mv.viewName = "member/info"
            mv.addObject("showModal", true) // 모달을 열도록 상태 추가
            mv.addObject("failed", "비밀번호가 일치하지 않습니다.")
        }
        return mv
    }

    @GetMapping("/signup")
    fun signup(mv: ModelAndView): ModelAndView {
        mv.addObject("memberJoinRequest", MemberJoinRequest())
        mv.viewName = "member/signup"
        return mv
    }

    @PostMapping("/signup")
    fun registerMember(
        @ModelAttribute @Valid joinMember: MemberJoinRequest,
        mv: ModelAndView
    ): ModelAndView {
        try {
            memberService.joinMember(joinMember)
            mv.viewName = "redirect:/members/login"
        } catch (e: BusinessException) {
            if (e.errorCode == MemberErrorCode.ALREADY_EXIST_EMAIL) {
                mv.addObject("errorMessage", e.message)
            }
            mv.viewName = "member/signup"

        }
        return mv
    }

    @GetMapping("/restore")
    fun reRegisterQ(mv: ModelAndView): ModelAndView {
        mv.viewName = "member/restore"
        return mv
    }
}

