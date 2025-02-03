package org.programmers.signalbuddy.domain.bookmark.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkRequest
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkResponse
import org.programmers.signalbuddy.domain.bookmark.service.BookmarkService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/bookmarks")
@Tag(name = "Bookmark API")
class BookmarkController(
    private val bookmarkService: BookmarkService
) {
    private val logger = KotlinLogging.logger {}

    @Operation(summary = "즐겨찾기 목록 조회", description = "Pagination")
    @GetMapping("/{memberId}")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 목록 조회 성공")
    fun getBookmarks(
        @PageableDefault(page = 0, size = 5) pageable: Pageable, @PathVariable memberId: Long
    ): ResponseEntity<Page<BookmarkResponse>> {
        val bookmarks = bookmarkService.findPagedBookmarks(pageable, memberId)
        return ResponseEntity.ok(bookmarks)
    }

    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기 등록 가능")
    @PostMapping
    @ApiResponse(responseCode = "201", description = "즐겨찾기 등록 성공")
    fun addBookmark(
        @RequestBody @Validated createRequest: BookmarkRequest, @CurrentUser user: CustomUser2Member
    ): ResponseEntity<BookmarkResponse> {
        logger.info { "Create Request: $createRequest" }
        val created = bookmarkService.createBookmark(createRequest, user)
        logger.info { "Create Response: $created" }
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Operation(summary = "즐겨찾기 수정", description = "즐겨찾기 수정 기능")
    @PatchMapping("{id}")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 수정 성공")
    fun updateBookmark(
        @RequestBody @Validated updateRequest: BookmarkRequest,
        @PathVariable id: Long,
        @CurrentUser user: CustomUser2Member
    ): ResponseEntity<BookmarkResponse> {
        logger.info { "Update Request: $updateRequest" }
        val updated: BookmarkResponse = bookmarkService.updateBookmark(updateRequest, id, user)
        logger.info { "Update Response: $updated" }
        return ResponseEntity.ok().body(updated)
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 삭제 기능")
    @DeleteMapping("{id}")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 삭제 성공")
    fun deleteBookmark(
        @PathVariable id: Long, @CurrentUser user: CustomUser2Member
    ): ResponseEntity<Unit> {
        logger.info { "Delete Request: $id" }
        bookmarkService.deleteBookmark(id, user)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}