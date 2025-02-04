package org.programmers.signalbuddy.domain.crossroad.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import lombok.RequiredArgsConstructor
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadApiResponse
import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.programmers.signalbuddy.domain.crossroad.entity.QCrossroad
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class QueryCrossroadRepositoryImpl : QueryCrossroadRepository {
    private val qCrossroad: QCrossroad = QCrossroad.crossroad

    private val jqf: JPAQueryFactory? = null

    override fun findNearByCrossroads(latitude: Double, longitude: Double): List<CrossroadApiResponse> {
        val res: MutableList<CrossroadApiResponse> = java.util.ArrayList<CrossroadApiResponse>()

        // ST_Distance_Sphere SQL 함수 사용
        val distanceExpression = "ST_Distance_Sphere(POINT({0}, {1}), coordinate)"

        val nearCrossroads: List<Crossroad> = jqf!!.selectFrom<Crossroad>(qCrossroad)
            .where(
                com.querydsl.core.types.dsl.Expressions.numberTemplate(
                    Double::class.java,
                    distanceExpression,
                    longitude,
                    latitude
                ).loe(80)
            ) // 거리 조건
            .fetch()

        for (near in nearCrossroads) {
            res.add(CrossroadApiResponse.toDto(near))
        }

        return res
    } /*
    SELECT station,
    ST_Distance_Sphere(@location, POINT(longitude, latitude) AS distance
    FROM Subway
    WHERE station = "신촌역";
*/
}
