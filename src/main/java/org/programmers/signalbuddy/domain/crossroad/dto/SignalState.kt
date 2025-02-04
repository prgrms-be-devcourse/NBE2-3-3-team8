package org.programmers.signalbuddy.domain.crossroad.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.persistence.Column
import lombok.AccessLevel
import lombok.AllArgsConstructor
import java.util.*


enum class SignalState (
        @Column(name = "state")
        @get:JvmName("getStateProperty")
        private val state: String?,
        val can_cross: Boolean?
    ){

    GREEN("protected-Movement-Allowed", true),  // 신호등이 녹색, 이동 보장 상태
    GRAY("permissive-Movement-Allowed", true),  // 신호등이 황색, 이동 가능 상태
    RED("stop-And-Remain", false); // 신호등이 적색, 정지 상태

    @JsonValue // 데이터 직렬화 java -> JSON
    fun getState(): String {
        return name.lowercase(Locale.getDefault())
    }

    companion object {
        @JsonCreator // 데이터 역직렬화 JSON -> java
        fun fromState(state: String): SignalState {
            for (signalState in entries) {
                if (signalState.state == state) {
                    return signalState
                }
            }
            throw IllegalArgumentException("Unknown name: $state")
        }
    }
}
