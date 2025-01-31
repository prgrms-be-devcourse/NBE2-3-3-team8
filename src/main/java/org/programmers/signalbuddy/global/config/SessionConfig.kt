package org.programmers.signalbuddy.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer

@Configuration
class SessionConfig {
    @Bean
    fun cookieSerializer(): CookieSerializer {
        val serializer = DefaultCookieSerializer()
        serializer.setCookieName("JSESSIONID")
        serializer.setCookiePath("/")
        serializer.setDomainNamePattern("^.+?(\\w+\\.[a-z]+)$")
        serializer.setUseBase64Encoding(false)
        return serializer
    }
}
