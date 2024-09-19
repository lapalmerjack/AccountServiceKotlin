package com.kotlin.AccountService.entities


data class DeletionResponse(val user: String, val status: String ="deleted")

data class UserResponse(val id: Long, val name: String, val lastName: String, val email: String)