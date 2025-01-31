package org.programmers.signalbuddy.global.util

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.LocalDateTime

object QueryDslUtil {
    /**
     * createAt이 startDate ~ endDate 범위로 설정
     *
     * @param path      QClass의 LocalDateTime 필드 <br></br>
     * ex) QFeedback.feedback.createdAt, QFeedback.feedback.updatedAt
     * @param startDate 조회하려는 시작 날짜 (null이면 없는 것으로 처리)
     * @param endDate   조회하려는 끝 날짜 (null이면 없는 것으로 처리)
     * @return 시작 날짜 ~ 끝 날짜 범위를 조건으로 설정하여 반환 (둘 다 null이면 전체 조회)
     */
    @JvmStatic
    fun betweenDates(
        path: DateTimePath<LocalDateTime?>,
        startDate: LocalDate?, endDate: LocalDate?
    ): BooleanExpression {
        var predicate: BooleanExpression? = null

        if (startDate != null) {
            predicate = path.goe(startDate.atStartOfDay())
        }

        if (endDate != null) {
            val endCondition = path.loe(endDate.atStartOfDay())
            predicate = if (predicate == null) endCondition else predicate.and(endCondition)
        }

        return predicate ?: Expressions.TRUE
    }

    /**
     * Pageable의 설정한 정렬을 QueryDSL에서 사용할 수 있게 조건을 정렬 조건을 반환
     *
     * @param pageable 쿼리 파라미터로 가져온 값들
     * @param type     정렬할 컬럼의 클래스 <br></br> ex) QFeedback.feedback.getType()
     * @param variable 정렬할 QClass의 Entity 필드명 <br></br> ex) "feedback"
     * @return 정렬 조건을 반환
     * @throws org.springframework.dao.InvalidDataAccessApiUsageException 잘못된 필드명을 입력하면 쿼리를 처리하는 중
     * 해당 예외가 발생한다.
     */
    @JvmStatic
    fun getOrderSpecifiers(
        pageable: Pageable, type: Class<*>?,
        variable: String
    ): Array<out OrderSpecifier<*>?> {
        val orderSpecifiers: MutableList<OrderSpecifier<*>> = ArrayList()

        // 정렬 조건이 없는 경우
        if (pageable.sort.isUnsorted) {
            return arrayOfNulls(1)
        }

        val pathBuilder: PathBuilder<*> = PathBuilder(type, variable)

        // Sort 정보를 기반으로 OrderSpecifier 생성
        for (order in pageable.sort) {
            val direction = if (order.isAscending) Order.ASC else Order.DESC
            orderSpecifiers.add(
                OrderSpecifier(
                    direction,
                    pathBuilder.getComparable(order.property, Comparable::class.java)
                )
            )
        }

        val results = orderSpecifiers.toTypedArray<OrderSpecifier<*>>()
        return results
    }
}
