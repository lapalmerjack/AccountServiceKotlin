package com.kotlin.AccountService.errors.customexceptions

class MinimumPasswordLengthException(message: String = "Password length must be at least 12 chars"):
    RuntimeException(message)

class NewPasswordMatchesOldPasswordException(message: String = "New password must not match old password"):
        RuntimeException(message)

class PasswordMatchesBannedPasswordException(message: String = "The password is in the hacker's database!"):
        RuntimeException(message)

class UserFoundException(message: String = "User already exists!"):
        RuntimeException(message)

class UserNotFoundException(message: String = "User not found"):
        RuntimeException(message)