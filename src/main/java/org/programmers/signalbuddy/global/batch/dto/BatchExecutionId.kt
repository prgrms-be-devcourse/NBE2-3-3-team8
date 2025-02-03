package org.programmers.signalbuddy.global.batch.dto

data class BatchExecutionId (
    var jobExecutionId: Long = 0,
    var stepExecutionId: Long = 0
)
