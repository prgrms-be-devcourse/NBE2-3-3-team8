package org.programmers.signalbuddy.domain.crossroad.repository

import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadApiResponse

interface QueryCrossroadRepository {
    fun findNearByCrossroads(latitude: Double, longitude: Double): List<CrossroadApiResponse>
}
