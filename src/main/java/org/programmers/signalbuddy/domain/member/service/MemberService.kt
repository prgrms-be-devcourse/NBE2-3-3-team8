package org.programmers.signalbuddy.domain.member.service

import jakarta.servlet.http.HttpServletRequest
import org.programmers.signalbuddy.domain.member.dto.MemberJoinRequest
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.dto.MemberUpdateRequest
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.mapper.MemberMapper
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.exception.BusinessException
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val awsFileService: AwsFileService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
    @Value("\${default.profile.image.path}") private val defaultProfileImagePath: String,
) {
    @Transactional(readOnly = true)
    fun getMember(id: Long): MemberResponse? {
        val member = memberRepository.findByIdOrNull(id) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )
        return MemberMapper.INSTANCE.toDto(member)
    }

    fun verifyPassword(password: String?, user: CustomUser2Member): Boolean {
        val member = memberRepository.findByIdOrNull(user.memberId) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )
        return bCryptPasswordEncoder.matches(password, member.password)
    }

    @Transactional
    fun updateMember(
        id: Long?, memberUpdateRequest: MemberUpdateRequest, request: HttpServletRequest
    ): MemberResponse? {
        val member = memberRepository.findByIdOrNull(id) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )
        val encodedPassword = encodedPassword(member.password)
        val saveProfileImage = saveProfileImageIfPresent(memberUpdateRequest.imageFile)

        member.updateMember(memberUpdateRequest, encodedPassword, saveProfileImage)
        updateSecurityContext(member, request)
        return MemberMapper.INSTANCE.toDto(member)
    }

    @Transactional
    fun deleteMember(id: Long?): MemberResponse? {
        val member = memberRepository.findByIdOrNull(id) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )
        member.softDelete()
        return MemberMapper.INSTANCE.toDto(member)
    }

    fun getProfileImage(filename: String): Resource {
        try {
            if (filename.isEmpty()) {
                return ClassPathResource(defaultProfileImagePath)
            }
            return awsFileService.getProfileImage(filename)
        } catch (e: BusinessException) {
            return ClassPathResource(defaultProfileImagePath)
        }
    }

    fun saveProfileImage(profileImage: MultipartFile): String {
        return awsFileService.saveProfileImage(profileImage)
    }

    private fun encodedPassword(password: String?): String? {
        if (password == null) {
            return null
        }
        return bCryptPasswordEncoder.encode(password)
    }

    private fun saveProfileImageIfPresent(imageFile: MultipartFile?): String? {
        if (imageFile == null) {
            return null
        }
        return saveProfileImage(imageFile)
    }

    private fun updateSecurityContext(member: Member, request: HttpServletRequest) {
        // CustomUserDetails 생성
        val userDetails = CustomUserDetails(
            member.memberId,
            member.email,
            member.password,
            member.profileImageUrl,
            member.nickname,
            member.role,
            member.memberStatus
        )

        // Authentication 객체 생성
        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            userDetails, null,  // 비밀번호는 이미 인증되었으므로 null
            userDetails.authorities
        )

        // SecurityContext 생성 및 Authentication 설정
        val securityContext = SecurityContextHolder.createEmptyContext()
        securityContext.authentication = authentication

        // SecurityContextHolder에 설정
        SecurityContextHolder.setContext(securityContext)

        // HttpSession에 SecurityContext 저장
        request.session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext)
        // HttpSession 갱신
        request.session.setAttribute("user", userDetails)
    }

    fun joinMember(memberJoinRequest: MemberJoinRequest): MemberResponse? {

        if(memberRepository.existsByEmail(memberJoinRequest.email)){
            throw BusinessException(MemberErrorCode.ALREADY_EXIST_EMAIL)
        }

        val profilePath = saveProfileImageIfPresent(memberJoinRequest.profileImageUrl)

        val joinMember = memberRepository.save(Member(
            email = memberJoinRequest.email,
            nickname = memberJoinRequest.nickname,
            password = bCryptPasswordEncoder.encode(memberJoinRequest.password),
            profileImageUrl = profilePath,
            memberStatus = MemberStatus.ACTIVITY,
            role = MemberRole.USER
        ))
        return MemberMapper.INSTANCE.toDto(joinMember)
    }
}
