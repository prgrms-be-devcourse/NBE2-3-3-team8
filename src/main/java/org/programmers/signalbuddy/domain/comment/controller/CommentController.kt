package org.programmers.signalbuddy.domain.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.programmers.signalbuddy.domain.comment.dto.CommentRequest
import org.programmers.signalbuddy.domain.comment.dto.CommentResponse
import org.programmers.signalbuddy.domain.comment.service.CommentService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.dto.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Comment API")
@RestController
@RequestMapping("/api/comments")
class CommentController (
    private val commentService: CommentService
) {

    @Operation(summary = "댓글 작성")
    @PostMapping("/write")
    fun writeComment(
        @Valid @RequestBody request: CommentRequest,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        commentService.writeComment(request, user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/{feedbackId}")
    fun searchCommentList(
        @PathVariable("feedbackId") feedbackId: Long,
        @PageableDefault(page = 0, size = 7) pageable: Pageable
    ): ResponseEntity<PageResponse<CommentResponse?>> =
        ResponseEntity.ok(commentService.searchCommentList(feedbackId, pageable))


    @Operation(summary = "댓글 수정")
    @PatchMapping("/{commentId}")
    fun modifyComment(
        @PathVariable("commentId") commentId: Long,
        @Valid @RequestBody request: CommentRequest,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        commentService.updateComment(commentId, request, user)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable("commentId") commentId: Long,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        commentService.deleteComment(commentId, user)
        return ResponseEntity.ok().build()
    }
}
