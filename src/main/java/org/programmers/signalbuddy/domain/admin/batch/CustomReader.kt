package org.programmers.signalbuddy.domain.admin.batch

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.QMember
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.springframework.batch.item.ItemReader
import java.time.LocalDateTime

open class CustomReader(
    entityManagerFactory: EntityManagerFactory,
    private val pageSize: Int
) : ItemReader<Member> {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManagerFactory.createEntityManager()) // 변경
    private var lastSeenId: Long? = null
    private var currentPage: List<Member> = emptyList()
    private var currentIndex = 0

    override fun read(): Member? {
        if (currentIndex >= currentPage.size) {
            fetchNextPage()
        }

        if (currentPage.isEmpty()) {
            return null
        }

        val member = currentPage[currentIndex]
        currentIndex++
        return member
    }

    private fun fetchNextPage() {
        val qMember = QMember.member

        currentPage = queryFactory.selectFrom(qMember)
            .where(
                lastSeenId?.let { qMember.memberId.gt(it) },
                qMember.memberStatus.eq(MemberStatus.WITHDRAWAL)
                    .and(qMember.updatedAt.loe(LocalDateTime.now().minusMonths(6)))
            )
            .orderBy(qMember.memberId.asc())
            .limit(pageSize.toLong())
            .fetch() ?: emptyList()

        if (currentPage.isNotEmpty()) {
            lastSeenId = currentPage.last().memberId
        }
        currentIndex = 0
    }
}
