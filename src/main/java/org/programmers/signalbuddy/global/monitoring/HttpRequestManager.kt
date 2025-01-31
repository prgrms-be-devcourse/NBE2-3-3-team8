package org.programmers.signalbuddy.global.monitoring

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
class HttpRequestManager (private val meterRegistry: MeterRegistry) {

    fun increase(signalId: Long) {
        val id = signalId.toString()
        Counter.builder("crossroad.call")
            .tag("class", this.javaClass.name)
            .tag("method", "getCrossRoadCall")
            .tag("id", id)
            .description("교차로 호출 횟수")
            .register(meterRegistry)
            .increment()
    }
}
