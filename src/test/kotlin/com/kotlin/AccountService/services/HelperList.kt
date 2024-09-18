package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.User

val dummyUserList = mutableListOf(
    User(
        name = "John",
        lastname = "Doe",
        email = "john@acme.com",
        password = "SecurePassword123"
    ),
    User(
        name = "Jane",
        lastname = "Smith",
        email = "jane@acme.com",
        password = "AnotherSecurePass12"
    ),
    User(
        name = "Michael",
        lastname = "Johnson",
        email = "michael@acme.com",
        password = "PasswordForMichael12"
    ),
    User(
        name = "Emily",
        lastname = "Williams",
        email = "emily@acme.com",
        password = "EmilySecurePassword!"
    ),
    User(
        name = "Robert",
        lastname = "Brown",
        email = "robert@acme.com",
        password = "RobertPasswordSecure1"
    )
)
