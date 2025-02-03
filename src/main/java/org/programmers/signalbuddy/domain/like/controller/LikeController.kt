package org.programmers.signalbuddy.domain.like.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import org.programmers.signalbuddy.domain.like.dto.LikeExistResponse
import org.programmers.signalbuddy.domain.like.service.LikeService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Like API")
@RestController
@RequestMapping("/api/feedbacks")
class LikeController (
    private val likeService: LikeService
) {

    @Operation(summary = "좋아요 추가")
    @PostMapping("/{feedbackId}/like")
    fun addLike(
        @PathVariable("feedbackId") feedbackId: Long,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        likeService.addLike(feedbackId, user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "좋아요 유무 확인")
    @GetMapping("/{feedbackId}/exist")
    fun existsLike(
        @PathVariable("feedbackId") feedbackId: Long,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<LikeExistResponse> =
        ResponseEntity.ok(likeService.existsLike(feedbackId, user))

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/{feedbackId}/like")
    fun deleteLike(
        @PathVariable("feedbackId") feedbackId: Long,
        @CurrentUser user: CustomUser2Member?
    ): ResponseEntity<Void> {
        likeService.deleteLike(feedbackId, user)
        return ResponseEntity.ok().build()
    }
}
