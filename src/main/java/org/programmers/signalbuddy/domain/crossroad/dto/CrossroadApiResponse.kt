package org.programmers.signalbuddy.domain.crossroad.dto

import org.locationtech.jts.geom.Point
import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.programmers.signalbuddy.domain.crossroad.service.PointUtil

data class CrossroadApiResponse(
    val crossroad: Crossroad,
    val crossroadApiId: String = crossroad.crossroadApiId,
    val name: String = crossroad.name,
    val lat: Double = crossroad.coordinate.y, // 위도
    val lng: Double = crossroad.coordinate.x // 경도
) {
    fun toPoint(): Point {
        return PointUtil.toPoint(this.lat, this.lng)
    }
}