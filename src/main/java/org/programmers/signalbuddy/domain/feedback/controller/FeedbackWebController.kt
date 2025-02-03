package org.programmers.signalbuddy.domain.feedback.controller

import jakarta.validation.Valid
import org.programmers.signalbuddy.domain.comment.service.CommentService
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.service.FeedbackService
import org.programmers.signalbuddy.domain.like.service.LikeService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/feedbacks")
class FeedbackWebController (
    private val feedbackService: FeedbackService,
    private val commentService: CommentService,
    private val likeService: LikeService
) {

    @GetMapping
    fun searchFeedbackList(
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
        @RequestParam(
            required = false, name = "answerStatus", defaultValue = "-1"
        ) answerStatus: Long,
        @CurrentUser user: CustomUser2Member?,
        mv: ModelAndView
    ): ModelAndView {
        val response = feedbackService.searchFeedbackList(
            pageable,
            answerStatus
        )
        mv.viewName = "feedback/list"
        mv.addObject("response", response)
        mv.addObject("answerStatus", answerStatus)
        mv.addObject("user", user)
        return mv
    }

    @GetMapping("/{feedbackId}")
    fun searchFeedbackDetail(
        @PathVariable("feedbackId") feedbackId: Long,
        @CurrentUser user: CustomUser2Member?,
        mv: ModelAndView
    ): ModelAndView {
        val feedback = feedbackService.searchFeedbackDetail(feedbackId)
        val commentPage = commentService.searchCommentList(
            feedbackId, PageRequest.of(0, 1000)
        ) // 페이지네이션 없이 모든 댓글을 가져오기 위함
        val isExistedLike = likeService.existsLike(feedbackId, user).status

        mv.viewName = "feedback/info"
        mv.addObject("feedback", feedback)
        mv.addObject("commentPage", commentPage)
        mv.addObject("user", user)
        mv.addObject("isExistedLike", isExistedLike)
        return mv
    }

    @GetMapping("/write")
    fun showWriteFeedbackPage(
        mv: ModelAndView
    ): ModelAndView {
        mv.viewName = "feedback/write"
        mv.addObject("request", FeedbackWriteRequest())
        return mv
    }

    @PostMapping("/write")
    fun writeFeedback(
        @ModelAttribute feedbackWriteRequest: @Valid FeedbackWriteRequest,
        @CurrentUser user: CustomUser2Member?,
        mv: ModelAndView
    ): ModelAndView {
        feedbackService.writeFeedback(feedbackWriteRequest, user)
        mv.viewName = "redirect:/feedbacks"
        return mv
    }

    @GetMapping("/edit/{feedbackId}")
    fun showEditFeedbackPage(
        @PathVariable("feedbackId") feedbackId: Long,
        mv: ModelAndView
    ): ModelAndView {
        val feedback = feedbackService.searchFeedbackDetail(feedbackId)
        mv.viewName = "feedback/edit"
        mv.addObject(
            "feedback", FeedbackWriteRequest(feedback.subject, feedback.content)
        )
        return mv
    }
}
