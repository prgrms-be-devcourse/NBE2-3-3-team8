package org.programmers.signalbuddy.domain.crossroad.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.*
import org.locationtech.jts.geom.Point
import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.programmers.signalbuddy.domain.crossroad.service.PointUtil

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
class CrossroadApiResponse(crossroad: Crossroad) {

    @JsonProperty("itstId")
    val crossroadApiId: String = crossroad.crossroadApiId

    @JsonProperty("itstNm")
    val name: String = crossroad.name

    @JsonProperty("mapCtptIntLat")
    val lat = crossroad.coordinate.y // 위도

    @JsonProperty("mapCtptIntLot")
    val lng = crossroad.coordinate.x // 경도

    fun toPoint(): Point {
        return PointUtil.toPoint(this.lat, this.lng)
    }
}
