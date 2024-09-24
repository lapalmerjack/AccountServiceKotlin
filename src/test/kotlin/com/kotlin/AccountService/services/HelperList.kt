package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.User

val dummyUserList = mutableListOf(
    User(
        id = 1,
        name = "John",
        lastname = "Doe",
        email = "john@acme.com",
        password = "SecurePassword123", // Normally you'd encode this password
        roles = mutableSetOf(
            Role(userRole = "ROLE_ADMINISTRATOR")
        )
    ),
    User(
        id = 2,
        name = "Jane",
        lastname = "Smith",
        email = "jane@acme.com",
        password = "AnotherSecurePass12",
        roles = mutableSetOf(
            Role(userRole = "ROLE_USER")
        )
    ),
    User(
        id = 3,
        name = "Michael",
        lastname = "Johnson",
        email = "michael@acme.com",
        password = "PasswordForMichael12",
        roles = mutableSetOf(
            Role(userRole = "ROLE_USER"),
            Role(userRole = "ROLE_AUDITOR")
        )
    ),
    User(
        id = 4,
        name = "Emily",
        lastname = "Williams",
        email = "emily@acme.com",
        password = "EmilySecurePassword!",
        roles = mutableSetOf(
            Role(userRole = "ROLE_USER")
        )
    ),
    User(
        id = 5,
        name = "Robert",
        lastname = "Brown",
        email = "robert@acme.com",
        password = "RobertPasswordSecure1",
        roles = mutableSetOf(
            Role(userRole = "ROLE_USER")
        )
    )
)
