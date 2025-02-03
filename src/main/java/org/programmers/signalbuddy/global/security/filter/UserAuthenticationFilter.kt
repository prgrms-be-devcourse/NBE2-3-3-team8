package org.programmers.signalbuddy.global.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.security.oauth.CustomOAuth2User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class UserAuthenticationFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        when (val user: Any? = request.session.getAttribute("user")) {
            is CustomUserDetails -> setAuthentication(user, user.authorities)
            is CustomOAuth2User ->  setAuthentication(user, user.authorities)

        }

        filterChain.doFilter(request, response)
    }

    private fun setAuthentication(
        principal: Any,
        authorities: Collection<GrantedAuthority?>
    ) {
        val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}
