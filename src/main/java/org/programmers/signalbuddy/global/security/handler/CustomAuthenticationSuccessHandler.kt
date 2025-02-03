package org.programmers.signalbuddy.global.security.handler

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.security.oauth.CustomOAuth2User
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import java.io.IOException

class CustomAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        var redirectUrl = "/"
        when (val principal = authentication.principal) {
            // 기본 로그인을 이용하는 사용자
            is CustomUserDetails -> {
                request.session.setAttribute("user", principal)
                if (principal.role.name.contains("ADMIN")) {
                    redirectUrl = "/admins"
                }
            }

            // 소셜 로그인을 이용하는 사용자
            is CustomOAuth2User -> request.session.setAttribute("user", principal)
        }

        request.session.maxInactiveInterval = 3600
        this.defaultTargetUrl = redirectUrl
        super.onAuthenticationSuccess(request, response, authentication)
    }
}