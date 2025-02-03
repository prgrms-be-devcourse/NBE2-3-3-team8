package org.programmers.signalbuddy.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig(
    @Value("\${spring.mail.smtp.host}")
    private val host: String,

    @Value("\${spring.mail.smtp.port}")
    private val port: Int,

    @Value("\${spring.mail.smtp.username}")
    private val username: String,

    @Value("\${spring.mail.smtp.password}")
    private val password: String,

    @Value("\${spring.mail.smtp.properties.mail.smtp.auth}")
    private val auth: Boolean,

    @Value("\${spring.mail.smtp.properties.mail.smtp.starttls.enable}")
    private val starttlsEnable: Boolean,

    @Value("\${spring.mail.smtp.properties.mail.smtp.starttls.required}")
    private val starttlsRequired: Boolean,
) {

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = username
        mailSender.password = password
        mailSender.defaultEncoding = "UTF-8"
        mailSender.javaMailProperties = properties

        return mailSender
    }

    private val properties: Properties
        get() {
            val properties = Properties()
            properties["mail.smtp.auth"] = auth
            properties["mail.smtp.starttls.enable"] = starttlsEnable
            properties["mail.smtp.starttls.required"] = starttlsRequired

            return properties
        }
}
