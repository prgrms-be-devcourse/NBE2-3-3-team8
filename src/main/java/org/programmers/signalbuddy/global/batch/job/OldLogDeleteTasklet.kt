package org.programmers.signalbuddy.global.batch.job

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.PreparedStatement
import java.time.LocalDate

open class OldLogDeleteTasklet (private val jdbcTemplate: JdbcTemplate) : Tasklet {

    companion object {
        private const val EXPIRED_DATE = 14L // 로그의 유효 기간 (일)

        // Batch Meta Table 로그 삭제 쿼리들 (순서대로 진행해야 함)
        private val DELETE_QUERIES = listOf(
            "DELETE FROM BATCH_STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN ( SELECT STEP_EXECUTION_ID FROM BATCH_STEP_EXECUTION WHERE START_TIME < ?)",
            "DELETE FROM BATCH_STEP_EXECUTION WHERE START_TIME < ?",
            "DELETE FROM BATCH_JOB_EXECUTION_CONTEXT WHERE JOB_EXECUTION_ID IN ( SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION WHERE CREATE_TIME < ?)",
            "DELETE FROM BATCH_JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID IN ( SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION WHERE CREATE_TIME < ?)",
            "DELETE FROM BATCH_JOB_EXECUTION WHERE CREATE_TIME < ?",
            "DELETE FROM BATCH_JOB_INSTANCE WHERE JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID FROM BATCH_JOB_EXECUTION)"
        )
    }

    @Transactional
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val oldDate = Date.valueOf(LocalDate.now().minusDays(EXPIRED_DATE))

        for (query in DELETE_QUERIES) {
            jdbcTemplate.update(query) { ps: PreparedStatement -> ps.setDate(1, oldDate) }
        }
        return RepeatStatus.FINISHED
    }
}
