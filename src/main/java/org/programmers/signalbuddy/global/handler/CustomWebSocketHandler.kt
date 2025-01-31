package org.programmers.signalbuddy.global.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

private val log = KotlinLogging.logger {}

@Component
class CustomWebSocketHandler : TextWebSocketHandler() {
    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info { "Received message: $message" }

        session.sendMessage(TextMessage("Server received: " + message.payload))
    }
}