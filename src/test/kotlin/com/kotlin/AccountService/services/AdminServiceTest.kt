package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.AdminCantDeleteItSelfException
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
import com.kotlin.AccountService.repositories.UserRepository
import io.mockk.*
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

private val userRepository: UserRepository = mockk()
private val adminService = AdminService(userRepository)

class AdminServiceTest {
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
    fun `list size should be five`() {

        every { userRepository.findAll() } returns dummyUserList

        val users = adminService.getUsers()

        // Assert the size of the list
        assertEquals(5, users.size)

        // Verify that the mock was called once
        verify(exactly = 1) { userRepository.findAll() }

    }


    @Test
    fun `An error is thrown if the password is too short`() {
        val myUser =   User(
            name = "John",
            lastname = "Doe",
            email = "john@acme.com",
            password = "short"
        )

        val violations = validator.validate(myUser)
        println(violations)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.message == "Password length must be 12 chars minimum!" })
    }

    @Test
    fun `A user can be found in database`() {
        val email = "john@acme.com"
        every { userRepository.findByEmailIgnoreCase(email)} returns myUsers[0]

        val user = adminService.retrieveEmployeeInfo(email)

        assertEquals(user.name, "John")
        assertEquals(user.password, "SecurePassword123")
    }

    @Test
    fun `An error is thrown if no user in database does not exist`() {
        val email = "nooneexists@hotmail.com"

        every { userRepository.findByEmailIgnoreCase(email)} returns null

        val exception = assertThrows<UserNotFoundException> {
            adminService.retrieveEmployeeInfo(email)
        }
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `A user can safely be deleted from the database`() {
        val email = "john@acme.com"
        val adminEmail = "adminemail@gmail.com"


        every { userRepository.deleteByEmail(email) } returns myUsers[0]

       adminService.deleteUserFromDatabase(adminEmail, email)

        verify(exactly = 1) { userRepository.deleteByEmail(email)}

    }

    @Test
    fun `An error is thrown when a user can't be found for deletion`() {
        val email = "john@acme.com"
        val adminEmail = "adminemail@gmail.com"


        every { userRepository.deleteByEmail(email)} returns null

        val exception = assertThrows<UserNotFoundException> {
            adminService.deleteUserFromDatabase(adminEmail, email)
        }

        assertEquals("User not found", exception.message)

    }

    @Test
    fun `Throws an error when an administrator tries to delete itself`() {
        val email = "adminemail@gmail.com"
        val adminEmail = "adminemail@gmail.com"

        val exception = assertThrows<AdminCantDeleteItSelfException> {
            adminService.deleteUserFromDatabase(adminEmail, email)
        }

        assertEquals("Can't remove ADMINISTRATOR role!", exception.message)


    }
}