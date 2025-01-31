package org.programmers.signalbuddy.global.config

import org.locationtech.jts.geom.GeometryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeometryConfig {
    @Bean
    fun geometryFactory(): GeometryFactory {
        return GeometryFactory()
    }
}