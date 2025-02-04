package org.programmers.signalbuddy.domain.crossroad.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.locationtech.jts.geom.Point
import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.programmers.signalbuddy.domain.crossroad.service.PointUtil

data class CrossroadApiResponse(
    @JsonProperty("itstId")
    val crossroadApiId: String,

    @JsonProperty("itstNm")
    val name: String,

    @JsonProperty("mapCtptIntLat")
    val lat: Double, // 위도

    @JsonProperty("mapCtptIntLot")
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