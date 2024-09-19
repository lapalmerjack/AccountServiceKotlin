package com.kotlin.AccountService.controllers


import com.kotlin.AccountService.services.UserService

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class Authentication(private val userService: UserService) {

}