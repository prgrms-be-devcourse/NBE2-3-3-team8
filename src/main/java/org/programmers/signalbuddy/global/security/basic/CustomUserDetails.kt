package org.programmers.signalbuddy.global.security.basic

import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val memberId: Long?,
    val email: String,
    private val password: String?,
    val profileImageUrl: String?,
    val nickname: String,
    val role: MemberRole,
    val status: MemberStatus
) : UserDetails {

    override fun isAccountNonExpired(): Boolean {
        return super.isAccountNonExpired()
    }

    override fun isAccountNonLocked(): Boolean {
        return super.isAccountNonLocked()
    }

    override fun isCredentialsNonExpired(): Boolean {
        return super.isCredentialsNonExpired()
    }

    override fun isEnabled(): Boolean {
        return super.isEnabled()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities: MutableCollection<GrantedAuthority> = ArrayList()
        authorities.add(GrantedAuthority { "ROLE_" + role.name })

        return authorities
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return email
    }
}
