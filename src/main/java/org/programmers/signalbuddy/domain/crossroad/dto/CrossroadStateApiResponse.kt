package org.programmers.signalbuddy.domain.crossroad.dto

data class CrossroadStateApiResponse(
    val crossroadApiId: String? = null,
    val nprs: Int = 0,
    val eprs: Int = 0,
    val sprs: Int = 0,
    val wprs: Int = 0,
    val neprs: Int = 0,
    val nwprs: Int = 0,
    val swprs: Int = 0,
    val seprs: Int = 0,
    val npsn: SignalState? = null,
    val epsn: SignalState? = null,
    val wpsn: SignalState? = null,
    val spsn: SignalState? = null,
    val nepsn: SignalState? = null,
    val nwpsn: SignalState? = null,
    val sepsn: SignalState? = null,
    val swpsn: SignalState? = null
)
