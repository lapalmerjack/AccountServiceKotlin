package com.kotlin.AccountService.entities

data class AuthorityEntity (val user: String, val role: String, val operation: Operations) {
    enum class Operations {GRANT, REMOVE}
}