package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.enums.Enums
import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.entities.UserResponse
import com.kotlin.AccountService.errors.customexceptions.*
import com.kotlin.AccountService.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    val logger = LoggerFactory.getLogger(this::class.java)

    fun registerUser(user: User): UserResponse {

        logger.info("Registering new user to database")
        throwErrorIfUserIsAlreadyInDatabase(user.email)

        logger.info("Checking for breached password")
        throwErrorIfPasswordIsBanned(user.password)
        val userRoles: MutableSet<Role> = setUserRole(user.roles)

        val userToBeSaved = user.copy(password = passwordEncoder.encode(user.password), roles = userRoles)

        val savedUser = userRepository.save(userToBeSaved)


        return UserResponse(id = savedUser.id!! ,
            name= savedUser.name,
            lastName =  savedUser.lastname,
            email = savedUser.email,
            roles = userRoles)
    }

    private fun setUserRole(userRoles: MutableSet<Role>): MutableSet<Role> {
        val role = if (userRepository.findAll().isEmpty()) "ROLE_ADMINISTRATOR" else "ROLE_USER";
        userRoles.add(Role(userRole = role))
        return userRoles

    }

    @Transactional
    fun changePassword(userEmail: String, newPassword: String) {

        logger.info("Checking password length")
        throwErrorIfPasswordLengthIsTooShort(newPassword)

        val fetchedUser = userRepository.findByEmailIgnoreCase(userEmail)
          ?: throw UserNotFoundException()

        logger.info("checking that passwords don't match")

        throwErrorIfNewPassWordMatchesOldPassword(fetchedUser.password, newPassword)

        val userWithUpdatedPassword = fetchedUser
            .copy(password = passwordEncoder.encode(newPassword))

        userRepository.save(userWithUpdatedPassword)

    }

    private fun throwErrorIfPasswordLengthIsTooShort(password: String) =
        checkCondition(condition = {password.length < 12},
            exception = MinimumPasswordLengthException()
        )


    private fun throwErrorIfUserIsAlreadyInDatabase(email: String) =
        checkCondition(
            condition = { userRepository.existsByEmail(email)},
            exception = UserFoundException()
        )


    private fun throwErrorIfPasswordIsBanned(password: String) =
        checkCondition(
            condition = { Enums.entries.any { it.breachedPassword == password } },
            exception = PasswordMatchesBannedPasswordException()
        )

    private fun throwErrorIfNewPassWordMatchesOldPassword
                (oldPassword: String, newPassword: String) =
        checkCondition(
            condition = { passwordEncoder.matches(oldPassword, newPassword)},
            exception = NewPasswordMatchesOldPasswordException()
        )
}
