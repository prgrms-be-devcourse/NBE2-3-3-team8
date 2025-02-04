package org.programmers.signalbuddy.domain.email.service

import jakarta.mail.MessagingException
import org.programmers.signalbuddy.domain.email.dto.EmailRequest
import org.programmers.signalbuddy.domain.email.dto.VerifyCodeRequest
import org.programmers.signalbuddy.domain.email.exception.EmailErrorCode
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.time.Duration
import java.util.*

@Service
class EmailService(
    private val memberRepository: MemberRepository,
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val expirePeriod = 60 * 10L

    // 이메일 발송
    fun sendEmail(emailRequest: EmailRequest) {

        val member = memberRepository.findByEmail(emailRequest.email) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )
        if (member.memberStatus != MemberStatus.WITHDRAWAL) {
            throw BusinessException(MemberErrorCode.ALREADY_EXIST_EMAIL)
        }

        val message = javaMailSender.createMimeMessage()
        val code = createCode()

        try {
            // 이메일 내용 기입
            val helper = MimeMessageHelper(message, true)
            helper.setTo(emailRequest.email)
            helper.setSubject("[signalBuddy]인증코드가 발송되었습니다.")
            helper.setText(setContent(code), true)
        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }

        // 인증 코드 저장
        codeSave(emailRequest.email, code)

        // 이메일 발송
        javaMailSender.send(message)
    }

    // 인증 코드 생성
    private fun createCode() = (Random().nextInt(888888) + 111111).toString()

    // 이메일 내용 작성
    private fun setContent(code: String): String {
        val context = Context().apply {setVariable("code", code)}
        val content: String = templateEngine.process("member/mail", context)

        require(!(content == null || content.isEmpty())) { "Generated content is empty" }
        return content
    }

    // 인증 코드 검증
    fun verifyCode(verifyCodeRequest: VerifyCodeRequest): Boolean? {
        val valueOperations = redisTemplate.opsForValue()
        val requestCode = verifyCodeRequest.code

        if (valueOperations.get(verifyCodeRequest.email) == requestCode) { return true }
        throw BusinessException(EmailErrorCode.NOT_MATCH_AUTHCODE)
    }

    // 인증 코드 저장
    private fun codeSave(email: String, code: String) {
        // 요청한 메일에 대한 인증코드가 존재하는 경우, 삭제한다.
        if (redisTemplate.hasKey(email)) {redisTemplate.delete(email)}

        val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()
        val expireDuration = Duration.ofSeconds(expirePeriod)
        valueOperations.set(email, code, expireDuration)
    }
}
