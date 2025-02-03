package org.programmers.signalbuddy.domain.bookmark.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkResponse
import org.programmers.signalbuddy.domain.bookmark.entity.QBookmark.bookmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class BookmarkRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : BookmarkRepositoryCustom {

    private val pageBookmarkDto = Projections.fields(
        BookmarkResponse::class.java,
        bookmark.bookmarkId,
        bookmark.address,
        bookmark.name,
        Expressions.numberTemplate(Double::class.java, "ST_X({0})", bookmark.coordinate)
            .`as`("lng"),
        Expressions.numberTemplate(Double::class.java, "ST_Y({0})", bookmark.coordinate)
            .`as`("lat"),
    )

    override fun findPagedByMember(pageable: Pageable, memberId: Long): Page<BookmarkResponse> {
        val responses: List<BookmarkResponse> = queryFactory.select(pageBookmarkDto).from(bookmark)
            .where(bookmark.member.memberId.eq(memberId)).offset(pageable.offset)
            .limit(pageable.pageSize.toLong()).orderBy(bookmark.bookmarkId.asc()).fetch()

        val count: Long = queryFactory.select(bookmark.count()).from(bookmark)
            .where(bookmark.member.memberId.eq(memberId)).fetchOne() ?: 0L

        return PageImpl(responses, pageable, count)
    }
}