package org.programmers.signalbuddy.domain.crossroad.dto

import org.locationtech.jts.geom.Point
import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.programmers.signalbuddy.domain.crossroad.service.PointUtil

data class CrossroadApiResponse(
    val crossroadApiId: String,
    val name: String,
    val lat: Double, // 위도
    val lng: Double // 경도
) {

    companion object {
        fun toDto(entity: Crossroad): CrossroadApiResponse {
            return CrossroadApiResponse(
                crossroadApiId = entity.crossroadApiId,
                name = entity.name,
                lat = entity.coordinate.y,
                lng = entity.coordinate.x
            )
        }
    }

    fun toPoint(): Point {
        return PointUtil.toPoint(this.lat, this.lng)
    }
}