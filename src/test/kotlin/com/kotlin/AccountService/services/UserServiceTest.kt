package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.PasswordMatchesBannedPasswordException
import com.kotlin.AccountService.errors.customexceptions.UserFoundException
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
import com.kotlin.AccountService.repositories.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

private val userRepository: UserRepository = mockk()
private val passwordEncoder: PasswordEncoder = mockk()
private val userService = UserService(userRepository, passwordEncoder)
class UserServiceTest {

    var myUsers: List<User> = mutableListOf()

    @BeforeEach
    fun setUp() {
        myUsers = dummyUserList
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
}