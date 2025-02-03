package org.programmers.signalbuddy.domain.crossroad.entity

import jakarta.persistence.*
import lombok.*
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadApiResponse

@Entity(name = "crossroads")
@Builder
@ToString
class Crossroad(response: CrossroadApiResponse) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var crossroadId: Long? = null

    @Column(nullable = false, unique = true)
    var crossroadApiId: String = response.crossroadApiId

    @Column(nullable = false)
    var name: String = response.name

    @Column(nullable = false)
    var coordinate = response.toPoint()
}