package org.programmers.signalbuddy.domain.feedback.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackResponse
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.service.FeedbackService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.dto.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "Feedback API")
@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController (
    private val feedbackService: FeedbackService
) {

    @Operation(summary = "피드백 작성")
    @PostMapping("/write")
    fun writeFeedback(
        @Valid @RequestBody feedbackWriteRequest: FeedbackWriteRequest,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<FeedbackResponse?> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(feedbackService.writeFeedback(feedbackWriteRequest, user))

    @Operation(summary = "관리자 피드백 목록 조회")
    @GetMapping("/admin")
    fun searchFeedbackList(
        @PageableDefault(
            page = 0, size = 10, sort = ["createAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam(
            required = false, name = "startDate"
        ) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @RequestParam(
            required = false, name = "endDate"
        ) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @RequestParam(
            required = false, name = "answerStatus", defaultValue = "-1"
        ) answerStatus: Long
    ): ResponseEntity<PageResponse<FeedbackResponse?>> =
        ResponseEntity.ok().body(
            feedbackService.searchFeedbackList(pageable, startDate, endDate, answerStatus)
        )

    @Operation(summary = "관리자 피드백 검색")
    @GetMapping(value = ["/admin"], params = ["keyword"])
    fun searchFeedbackList(
        @PageableDefault(
            page = 0, size = 10, sort = ["createAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam(name = "keyword") keyword: String?,
        @RequestParam(
            required = false, name = "answerStatus", defaultValue = "-1"
        ) answerStatus: Long
    ): ResponseEntity<PageResponse<FeedbackResponse>> =
        ResponseEntity.ok()
            .body(feedbackService.searchByKeyword(pageable, keyword, answerStatus))

    @Operation(summary = "피드백 목록 조회")
    @GetMapping
    fun searchFeedbackList(
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
        @RequestParam(
            required = false, name = "answerStatus", defaultValue = "-1"
        ) answerStatus: Long
    ): ResponseEntity<PageResponse<FeedbackResponse?>> =
        ResponseEntity.ok().body(feedbackService.searchFeedbackList(pageable, answerStatus))

    @Operation(summary = "피드백 상세 조회")
    @GetMapping("/{feedbackId}")
    fun searchFeedbackDetail(
        @PathVariable("feedbackId") feedbackId: Long
    ): ResponseEntity<FeedbackResponse> =
        ResponseEntity.ok().body(feedbackService.searchFeedbackDetail(feedbackId))


    @Operation(summary = "피드백 수정")
    @PatchMapping("/{feedbackId}")
    fun updateFeedback(
        @PathVariable("feedbackId") feedbackId: Long,
        @Valid @RequestBody feedbackWriteRequest: FeedbackWriteRequest,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        feedbackService.updateFeedback(feedbackId, feedbackWriteRequest, user)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "피드백 삭제")
    @DeleteMapping("/{feedbackId}")
    fun deleteFeedback(
        @PathVariable("feedbackId") feedbackId: Long,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        feedbackService.deleteFeedback(feedbackId, user)
        return ResponseEntity.ok().build()
    }
}
