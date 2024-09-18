package com.kotlin.AccountService.controllers

import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.services.AdminService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
class Administrator(private val adminService: AdminService) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/users")
    fun getAllUsers(@Valid @AuthenticationPrincipal user: UserDetails): ResponseEntity<List<User>> {

        logger.info("Retrieving all users")
        val users = adminService.getUsers()

        return ResponseEntity(users, HttpStatus.OK)
    }

    @GetMapping
    fun getUser(@AuthenticationPrincipal user: UserDetails): ResponseEntity<User> {
        logger.info("Retrieving user: ")
        val fetchedUser = adminService.retrieveEmployeeInfo(user.username)

        logger.info("User {} found", fetchedUser.email)

        return ResponseEntity(fetchedUser, HttpStatus.OK)

    }


}