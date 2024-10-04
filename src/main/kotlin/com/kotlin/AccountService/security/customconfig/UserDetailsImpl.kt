package com.kotlin.AccountService.security.customconfig

import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.User
import org.hibernate.type.TrueFalseConverter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.function.Consumer

class UserDetailsImpl(private val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        val roles: Set<Role> = user.roles
        val authorities: MutableList<SimpleGrantedAuthority?> = ArrayList()

        roles.forEach(Consumer<Role> { role: Role ->
            authorities.add(SimpleGrantedAuthority(role.userRole)) })

        return authorities
    }

    override fun getPassword(): String =  user.password


    override fun getUsername(): String =  user.email


    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = user.isAccountUnLocked

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
