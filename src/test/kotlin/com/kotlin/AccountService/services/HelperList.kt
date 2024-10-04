package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.Salary
import com.kotlin.AccountService.entities.User

val dummyUserList = mutableListOf(
    User(
        id = 1,
        name = "John",
        lastname = "Doe",
        email = "john@acme.com",
        password = "SecurePassword123",
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
    ).apply {
        addPayment(Salary(employee = email, period = "07-2001", salary = 800026, user = this))
        addPayment(Salary(employee = email, period = "02-2000", salary = 700083, user = this))
    },
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
    ).apply {
        addPayment(Salary(employee = email, period = "01-2024", salary = 600047, user = this))
        addPayment(Salary(employee = email, period = "02-2024", salary = 600330, user = this))
    },
    User(
        id = 4,
        name = "Emily",
        lastname = "Williams",
        email = "emily@acme.com",
        password = "EmilySecurePassword!",
        roles = mutableSetOf(
            Role(userRole = "ROLE_USER")
        ),
        isAccountUnLocked = false,
        loginAttempts = 3
    ).apply {
        addPayment(Salary(employee = email, period = "05-2022", salary = 550017, user = this))
        addPayment(Salary(employee = email, period = "04-2017", salary = 550450, user = this))
    },
    User(
        id = 5,
        name = "Robert",
        lastname = "Brown",
        email = "robert@acme.com",
        password = "RobertPasswordSecure1",
        roles = mutableSetOf(
            Role(userRole = "ROLE_USER")
        )
    ).apply {
        addPayment(Salary(employee = email, period = "01-2020", salary = 480190, user = this))
        addPayment(Salary(employee = email, period = "08-2000", salary = 480038, user = this))
    }
)
