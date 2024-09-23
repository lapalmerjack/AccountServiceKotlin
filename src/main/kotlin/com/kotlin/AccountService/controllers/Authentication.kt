package com.kotlin.AccountService.controllers


import com.kotlin.AccountService.entities.PasswordChanged
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.entities.UserResponse
import com.kotlin.AccountService.services.UserService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log

@RestController
@RequestMapping("api/auth")
class Authentication(private val userService: UserService) {
    val logger = LoggerFactory.getLogger(this::class.java)


    @PostMapping("/signup")
    fun registerNewUser( @RequestBody user: User): ResponseEntity<UserResponse> {
        println("$user is the user")
       val registerUser = userService.registerUser(user)

        return ResponseEntity(registerUser, HttpStatus.OK)

    }

    @PostMapping("/changepass")
    fun changePassword(@Valid @AuthenticationPrincipal user: UserDetails,
                       @RequestBody newPassword: Map<String, String>) : ResponseEntity<PasswordChanged>{
        logger.info("Updated {} password", user.username)

             newPassword["new_password"]
            ?.let { userService.changePassword(user.username, it) }

        return ResponseEntity(PasswordChanged(), HttpStatus.OK)
    }

}