package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.BreachedPasswords
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.entities.UserResponse
import com.kotlin.AccountService.errors.customexceptions.PasswordMatchesBannedPasswordException
import com.kotlin.AccountService.errors.customexceptions.UserFoundException
import com.kotlin.AccountService.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder

class UserService(private val userRepository: UserRepository, private val passWordEncoder: PasswordEncoder) {



    val logger = LoggerFactory.getLogger(this::class.java)

    fun registerUser(user: User): UserResponse {

        logger.info("Registering new user to database")
        checkIfUserExists(user.email)

        logger.info("Checking for breached password")
        checkIfPasswordIsBanned(user.password)




        return UserResponse(1,user.name, user.lastname, user.email)
    }

    private fun checkIfUserExists(email: String) = userRepository.existsByEmail(email)
        .takeIf { it }?.let { throw UserFoundException() }

    private fun checkIfPasswordIsBanned(password: String) =
        BreachedPasswords.entries.any { it.breachedPassword == password }
            .takeIf { it }?.let { throw PasswordMatchesBannedPasswordException() }

}
