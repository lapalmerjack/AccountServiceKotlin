package com.kotlin.AccountService.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
import com.kotlin.AccountService.repositories.UserRepository
import com.kotlin.AccountService.security.customconfig.CustomAccessDeniedHandler
import com.kotlin.AccountService.security.customconfig.UserDetailsImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import java.util.*

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(@Autowired private val restAuthEntryPoint: RestAuthEntryPoint,  private val userRepository: UserRepository) {
    val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // LOGGER.info("Beginning Security")
        http
            .csrf { csrf ->
                csrf.disable()
                csrf.ignoringRequestMatchers(PathRequest.toH2Console())
            }
            .headers { headers ->
                headers
                    .frameOptions { it.disable() }
            }
            .authorizeHttpRequests { it
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/actuator/shutdown").permitAll()
                    .requestMatchers("/api/admin/user/**").hasAnyAuthority("ROLE_ADMINISTRATOR")
                    .requestMatchers("/api/acct/payments").hasAnyAuthority("ROLE_ACCOUNTANT")
                    .anyRequest().authenticated()
            }
            .exceptionHandling { it.accessDeniedHandler(accessDeniedHandler())
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .httpBasic { it.authenticationEntryPoint(restAuthEntryPoint)
            }

         logger.info("Security set up complete")

        return http.build()
    }

    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler {
        return CustomAccessDeniedHandler(ObjectMapper())
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsService(userDetailsService())

        return provider
    }


    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { email: String ->
            // LOGGER.info("Checking if user exists");
            val databaseUser: User = userRepository.findByEmailIgnoreCase(email.lowercase(Locale.getDefault()))
                ?: throw UserNotFoundException()

            //   LOGGER.info("User found {}", databaseUser.getLastname());
            if (!databaseUser.isAccountUnLocked) {
                throw LockedException("User account is locked")
            }
            UserDetailsImpl(databaseUser)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}