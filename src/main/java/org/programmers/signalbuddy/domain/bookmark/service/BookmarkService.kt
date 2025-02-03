package org.programmers.signalbuddy.domain.bookmark.service

import lombok.extern.slf4j.Slf4j
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkRequest
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkResponse
import org.programmers.signalbuddy.domain.bookmark.exception.BookmarkErrorCode
import org.programmers.signalbuddy.domain.bookmark.mapper.BookmarkMapper
import org.programmers.signalbuddy.domain.bookmark.repository.BookmarkRepository
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val geometryFactory: GeometryFactory,
    private val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun getBookmark(bookmarkId: Long): BookmarkResponse {
        val bookmark = bookmarkRepository.findById(bookmarkId)
            .orElseThrow { BusinessException(BookmarkErrorCode.NOT_FOUND_BOOKMARK) }
        return BookmarkMapper.INSTANCE.toDto(bookmark)
    }

    @Transactional(readOnly = true)
    fun findPagedBookmarks(pageable: Pageable, memberId: Long): Page<BookmarkResponse>? {
        return bookmarkRepository.findPagedByMember(pageable, memberId)
    }

    @Transactional
    fun createBookmark(createRequest: BookmarkRequest, user: CustomUser2Member): BookmarkResponse {
        val member = getMember(user)
        val point = toPoint(createRequest.lat, createRequest.lng)
        val entity = BookmarkMapper.INSTANCE.toEntity(createRequest, point, member)
        val bookmark = bookmarkRepository.save(entity)
        return BookmarkMapper.INSTANCE.toDto(bookmark)
    }

    @Transactional
    fun updateBookmark(
        updateRequest: BookmarkRequest, id: Long, user: CustomUser2Member
    ): BookmarkResponse {
        val bookmark = bookmarkRepository.findByIdOrNull(id) ?: throw BusinessException(
            BookmarkErrorCode.NOT_FOUND_BOOKMARK
        )

        val member = getMember(user)
        if (member.memberId != bookmark.member!!.memberId) {
            throw BusinessException(BookmarkErrorCode.UNAUTHORIZED_MEMBER_ACCESS)
        }

        val point = toPoint(updateRequest.lat, updateRequest.lng)

        updateRequest.name?.let { bookmark.update(point, updateRequest.address, it) }
        return BookmarkMapper.INSTANCE.toDto(bookmark)
    }

    @Transactional
    fun deleteBookmark(id: Long, user: CustomUser2Member) {
        val bookmark = bookmarkRepository.findByIdOrNull(id) ?: throw BusinessException(
            BookmarkErrorCode.NOT_FOUND_BOOKMARK
        )
        val member = getMember(user)
        if (member.memberId != bookmark.member!!.memberId) {
            throw BusinessException(BookmarkErrorCode.UNAUTHORIZED_MEMBER_ACCESS)
        }

        bookmarkRepository.delete(bookmark)
    }

    private fun toPoint(lat: Double, lng: Double): Point {
        if (lng < -180 || lng > 180 || lat < -90 || lat > 90) {
            throw BusinessException(BookmarkErrorCode.INVALID_COORDINATES)
        }
        return geometryFactory.createPoint(Coordinate(lng, lat))
    }

    private fun getMember(user: CustomUser2Member): Member {
        return memberRepository.findByIdOrNull(user.memberId) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )
    }
}
