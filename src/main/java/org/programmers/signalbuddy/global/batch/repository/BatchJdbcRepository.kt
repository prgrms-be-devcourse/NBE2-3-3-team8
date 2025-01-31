package org.programmers.signalbuddy.global.batch.repository

import org.programmers.signalbuddy.global.batch.dto.BatchExecutionId
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.SQLException

@Repository
class BatchJdbcRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    /**
     * STEP_EXECUTION_ID로 STEP 관련 BATCH META TABLE의 데이터를 벌크 연산으로 삭제
     *
     * @param batchTableName 삭제할 배치 테이블명
     * @param executionIds  STEP_EXECUTION_ID, JOB_EXECUTION_ID 목록
     */
    fun deleteAllByStepExecutionIdInBatch(
        batchTableName: String,
        executionIds: List<BatchExecutionId>
    ) {
        val sql = "DELETE FROM $batchTableName WHERE STEP_EXECUTION_ID = ?"

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            @Throws(SQLException::class)
            override fun setValues(ps: PreparedStatement, i: Int) {
                val stepExecutionId = executionIds[i].stepExecutionId
                ps.setLong(1, stepExecutionId)
            }

            override fun getBatchSize(): Int {
                return executionIds.size
            }
        })
    }

    /**
     * JOB_EXECUTION_ID로 JOB 관련 BATCH META TABLE의 데이터를 벌크 연산으로 삭제
     *
     * @param batchTableName 삭제할 배치 테이블명
     * @param executionIds  STEP_EXECUTION_ID, JOB_EXECUTION_ID 목록
     */
    fun deleteAllByJobExecutionIdInBatch(
        batchTableName: String,
        executionIds: List<BatchExecutionId>
    ) {
        val sql = "DELETE FROM $batchTableName WHERE JOB_EXECUTION_ID = ?"

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            @Throws(SQLException::class)
            override fun setValues(ps: PreparedStatement, i: Int) {
                val jobExecutionId = executionIds[i].jobExecutionId
                ps.setLong(1, jobExecutionId)
            }

            override fun getBatchSize(): Int {
                return executionIds.size
            }
        })
    }
}
