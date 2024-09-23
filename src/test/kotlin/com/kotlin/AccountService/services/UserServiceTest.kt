package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.NewPasswordMatchesOldPasswordException
import com.kotlin.AccountService.errors.customexceptions.PasswordMatchesBannedPasswordException
import com.kotlin.AccountService.errors.customexceptions.UserFoundException
import com.kotlin.AccountService.repositories.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


private val userRepository: UserRepository = mockk()
private val passwordEncoder: PasswordEncoder = mockk()
private val userService = UserService(userRepository, passwordEncoder)
class UserServiceTest {
    private lateinit var validator: Validator

    var myUsers: List<User> = mutableListOf()

    @BeforeEach
    fun setUp() {
        myUsers = dummyUserList
        val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = validatorFactory.validator


    }

    @AfterEach
    fun tearDown() {
        clearMocks(userRepository)

    }

    @Test
    fun `An error is thrown if the user is already found in the database`() {
        val user = User( "maurice", "Skinner", "gooddude@gmail.com", "thisisalongpassword")
        every { userRepository.existsByEmail(any()) } returns true

        val exception = assertThrows<UserFoundException> {
           userService.registerUser(user)
        }

        assertEquals("User already exists!", exception.message)
    }

    @Test
    fun `Error is thrown if password is a forbidden password ` () {
        val user = User( "maurice", "Skinner", "gooddude@gmail.com", "PasswordForJune")
        every { userRepository.existsByEmail(any()) } returns false

        val exception = assertThrows<PasswordMatchesBannedPasswordException> {
            userService.registerUser(user)
        }
        assertEquals("The password is in the hacker's database!", exception.message)

    }

    @Test
    fun `User is successfully saved to the database`() {
        val myUser =   User(
            name = "John",
            lastname = "Doe",
            email = "john@acme.com",
            password = "SecurePassword123"
        )
        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(any()) } returns dummyUserList[0]

        userService.registerUser(myUser)
        verify(exactly = 1) { userRepository.save(any()) }
    }



    @Test
    fun `User is able to change its password`() {
        val myUser =   User(
            name = "John",
            lastname = "Doe",
            email = "john@acme.com",
            password = "SecurePassword123"
        )

        val newPassword = "ThisIsNotASecurePassword"
        val updatedUser = myUser.copy(password = newPassword)
        every { passwordEncoder.matches(any(), any()) } returns false
        every { passwordEncoder.encode(any()) } returns newPassword
        every { userRepository.findByEmailIgnoreCase(any()) } returns myUser
        every { userRepository.save(any())} returns updatedUser


        userService.changePassword(myUser.email, newPassword)

        assertEquals("ThisIsNotASecurePassword", updatedUser.password)

        verify(exactly = 1) { userRepository.save(match { user -> user.password ==
                newPassword && user.email == myUser.email }) }
        verify(exactly = 1) { userRepository.findByEmailIgnoreCase(myUser.email) }
    }

    @Test
    fun `An error is thrown if the new password is identical to old password`() {

        val myEncoder: PasswordEncoder = BCryptPasswordEncoder()
        val rawPassword = "SecurePassword123"
        val encodedPassword = myEncoder.encode(rawPassword)

        val myUser =   User(
            name = "John",
            lastname = "Doe",
            email = "john@acme.com",
            password = encodedPassword
        )
        val newPassword = "SecurePassword123"

        every { userRepository.findByEmailIgnoreCase(any()) } returns myUser
        every { passwordEncoder.matches(myUser.password, newPassword) } returns true


        val exception = assertThrows<NewPasswordMatchesOldPasswordException> {
            userService.changePassword(myUser.email, newPassword)
        }

        assertEquals("New password must not match old password", exception.message)

    }


}