package org.programmers.signalbuddy.domain.member.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.programmers.signalbuddy.domain.email.dto.EmailRequest
import org.programmers.signalbuddy.domain.email.dto.VerifyCodeRequest
import org.programmers.signalbuddy.domain.email.service.EmailService
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackResponse
import org.programmers.signalbuddy.domain.feedback.service.FeedbackService
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.dto.MemberUpdateRequest
import org.programmers.signalbuddy.domain.member.service.MemberService
import org.springframework.core.io.Resource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/members")
@Tag(name = "Member API")
class MemberController(
    private val memberService: MemberService,
    private val feedbackService: FeedbackService,
    private val emailService: EmailService
) {
    private val logger = KotlinLogging.logger {}

    @Operation(summary = "사용자 조회 API")
    @GetMapping("{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        val member = memberService.getMember(id)
        return ResponseEntity.ok(member)
    }

    @Operation(summary = "사용자 수정 API")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("{id}")
    fun updateMember(
        @PathVariable id: Long,
        @RequestPart(value = "email", required = false) email: String,
        @RequestPart(value = "nickname", required = false) nickname: String,
        @RequestPart(value = "password", required = false) password: String?,
        @RequestPart(value = "imageFile", required = false) imageFile: MultipartFile?,
        request: HttpServletRequest
    ): ResponseEntity<MemberResponse> {
        val memberUpdateRequest = MemberUpdateRequest(
            email = email,
            password = password,
            nickname = nickname,
            imageFile = imageFile,
        )
        val updated = memberService.updateMember(id, memberUpdateRequest, request)
        logger.info { "Member updated: $updated" }
        return ResponseEntity.status(HttpStatus.CREATED).body(updated)
    }

    @Operation(summary = "사용자 탈퇴 API")
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @DeleteMapping("{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        logger.info { "id : $id" }
        val deleted = memberService.deleteMember(id)
        logger.info { "Member soft deleted: $deleted" }
        return ResponseEntity.status(HttpStatus.OK).body(deleted)
    }

    @GetMapping("/files/{filename}")
    @Operation(summary = "이미지 조회 API", description = "페이지에서 이미지를 조회할 수 있는 API")
    @ApiResponse(responseCode = "200", description = "이미지 파일 리턴")
    fun getImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val image = memberService.getProfileImage(filename)
        return ResponseEntity.status(HttpStatus.OK).body(image)
    }

    @Operation(summary = "해당 사용자 피드백 목록 조회 API")
    @ApiResponse(responseCode = "200", description = "피드백 목록 조회 성공")
    @GetMapping("{id}/feedbacks")
    fun getFeedbacks(
        @PathVariable id: Long,
        @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<Page<FeedbackResponse?>> {
        logger.info { "id : {} $id" }
        val feedbacks = feedbackService.findPagedFeedbacksByMember(id, pageable)
        return ResponseEntity.ok(feedbacks)
    }

    @Operation(summary = "인증 코드 요청 API")
    @PostMapping("/auth-code")
    fun authCode(@RequestBody email: EmailRequest): ResponseEntity<Unit> {
        emailService.sendEmail(email)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/verify-code")
    fun verifyCode(@RequestBody verifyCodeRequest: VerifyCodeRequest): ResponseEntity<Unit> {
        emailService.verifyCode(verifyCodeRequest)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/restore")
    fun restore(@RequestBody email: EmailRequest): ResponseEntity<Unit> {
        memberService.restore(email)
        return ResponseEntity.ok().build()
    }
}