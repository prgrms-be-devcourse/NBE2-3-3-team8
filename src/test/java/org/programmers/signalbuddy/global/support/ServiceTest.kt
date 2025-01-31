package org.programmers.signalbuddy.global.support

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.programmers.signalbuddy.global.config.DataInitializer
import org.programmers.signalbuddy.global.db.MariaDBTestContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@Import(DataInitializer::class)
@ExtendWith(SpringExtension::class)
abstract class ServiceTest : MariaDBTestContainer {

    @Autowired
    private lateinit var dataInitializer: DataInitializer

    @BeforeEach
    fun delete() {
        dataInitializer.clear()
    }
}
