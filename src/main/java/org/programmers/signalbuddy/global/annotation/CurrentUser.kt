package org.programmers.signalbuddy.global.annotation

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(
    AnnotationRetention.RUNTIME
)
@AuthenticationPrincipal(expression = "#this == 'anonymous' ? null:new org.programmers.signalbuddy.global.dto.CustomUser2Member(#this)")
annotation class CurrentUser 
