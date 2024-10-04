package com.kotlin.AccountService.entities


data class DeletionResponse(val user: String, val status: String ="deleted")

data class UserResponse(val id: Long, val name: String, val lastName: String, val email: String, val roles: Set<Role>)

data class PasswordChanged(val message: String = "password updated")

data class SalaryResponse(val firstName: String, val lastName: String, val formattedPeriod: String, val formattedSalary: String)