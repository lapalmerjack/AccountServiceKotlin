package com.kotlin.AccountService.entities

import jakarta.persistence.Entity


data class PasswordChanged(val message: String = "password updated")