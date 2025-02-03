package org.programmers.signalbuddy.global.config

import org.programmers.signalbuddy.global.security.filter.UserAuthenticationFilter
import org.programmers.signalbuddy.global.security.handler.CustomAuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.programmers.signalbuddy.global.security.oauth.CustomOAuth2UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService:CustomOAuth2UserService
) {
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun customAuthenticationSuccessHandler(): CustomAuthenticationSuccessHandler {
        return CustomAuthenticationSuccessHandler()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // 인가 설정

        http
            .authorizeHttpRequests { auth ->
                auth // 문서화 api
                    .requestMatchers(
                        "/",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/swagger-resources/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/ws/**",
                        "/webjars/**"
                    ).permitAll() // 로그인, 회원가입
                    .requestMatchers(
                        "/members/login", "admins/login", "/api/members/join",
                        "/api/admins/join", "/members/signup", "/api/members/files/**"
                    ).permitAll() // 북마크
                    .requestMatchers("/api/bookmarks/**", "/bookmarks/**").hasRole("USER") // 댓글
                    .requestMatchers(HttpMethod.GET, "/api/comments").permitAll() // 교차로
                    .requestMatchers("/api/crossroads/save").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/crossroads/**").permitAll() // 피드백
                    .requestMatchers(HttpMethod.GET, "/api/feedbacks", "/feedbacks/**")
                    .permitAll() // 회원
                    .requestMatchers("/api/admins/**", "/admins/members/**").hasRole("ADMIN")
                    .requestMatchers("/api/members/**", "/members/**")
                    .hasRole("USER") // Prometheus 엔드포인트 허용
                    .requestMatchers("/actuator/prometheus").permitAll() // Health Check
                    .requestMatchers("/actuator/health").permitAll()
                    .anyRequest().authenticated()
            }

        // 기본 로그인 관련 설정
        http
            .formLogin { auth: FormLoginConfigurer<HttpSecurity?> ->
                auth
                    .loginPage("/members/login")
                    .loginProcessingUrl("/login")
                    .successHandler(customAuthenticationSuccessHandler())
                    .permitAll()
            }

        // 소셜 로그인 관련 설정
        http
            .oauth2Login { oauth ->
                oauth
                    .loginPage("/login")
                    .userInfoEndpoint { userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2UserService)
                    }
                    .successHandler(customAuthenticationSuccessHandler())
                    .permitAll()
            }


        // 로그아웃 관련 설정
        http
            .logout { auth: LogoutConfigurer<HttpSecurity?> ->
                auth
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/members/login")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
            }

        // 세션 관리 설정
        http
            .sessionManagement { sessionManagement: SessionManagementConfigurer<HttpSecurity?> ->
                // 세션 생성 정책
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                // 중복 로그인 처리
                sessionManagement.maximumSessions(1)
                    .maxSessionsPreventsLogin(true)

                // 세션 고정 공격 방어
                sessionManagement.sessionFixation().changeSessionId()
            }

        // csrf 비활성화
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }

        // 커스텀 필터 추가
        http
            .addFilterBefore(
                UserAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}

