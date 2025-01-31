package org.programmers.signalbuddy.global.config

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@TestComponent
class DataInitializer {
    private val tableNames: MutableList<String> = ArrayList()

    @Autowired
    private val dataSource: DataSource? = null

    @PersistenceContext
    private val entityManager: EntityManager? = null

    companion object {
        private const val OFF = 0
        private const val ON = 1
        private const val COLUMN_INDEX = 1
    }

    private fun findDatabaseTableNames() {
        try {
            dataSource!!.connection.createStatement().use { statement ->
                val resultSet = statement.executeQuery("SHOW TABLES")
                while (resultSet.next()) {
                    val tableName = resultSet.getString(COLUMN_INDEX)
                    tableNames.add(tableName)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun truncate() {
        setForeignKeyCheck(OFF)
        for (tableName in tableNames) {
            if (tableName.startsWith("BATCH")) {
                continue
            }
            entityManager!!.createNativeQuery(String.format("TRUNCATE TABLE %s", tableName))
                .executeUpdate()
        }
        setForeignKeyCheck(ON)
    }

    private fun setForeignKeyCheck(mode: Int) {
        entityManager!!.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = %d", mode))
            .executeUpdate()
    }

    @Transactional
    fun clear() {
        if (tableNames.isEmpty()) {
            findDatabaseTableNames()
        }
        entityManager!!.clear()
        truncate()
    }
}
