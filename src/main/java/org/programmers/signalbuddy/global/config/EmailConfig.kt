package org.programmers.signalbuddy.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig{

    @Value("\${spring.mail.smtp.host}")
    lateinit var host: String

    @Value("\${spring.mail.smtp.port}")
    lateinit var port: String

    @Value("\${spring.mail.smtp.username}")
    lateinit var username: String

    @Value("\${spring.mail.smtp.password}")
    lateinit var password: String

    @Value("\${spring.mail.smtp.properties.mail.smtp.auth}")
    lateinit var auth: String

    @Value("\${spring.mail.smtp.properties.mail.smtp.starttls.enable}")
    lateinit var starttlsEnable: String

    @Value("\${spring.mail.smtp.properties.mail.smtp.starttls.required}")
    lateinit var starttlsRequired: String

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port.toInt()
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
