package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
import com.kotlin.AccountService.repositories.UserRepository
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

private val userRepository: UserRepository = mockk()
private val adminService = AdminService(userRepository)

class AdminServiceTest {

    var myUsers: List<User> = mutableListOf()

    @BeforeEach
    fun setUp() {
        myUsers = mutableListOf()
    }

    @AfterEach
    fun tearDown() {
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
    fun `A user can be found in database`() {
        val email = "john@acme.com"
        every { userRepository.findByEmailIgnoreCase(email)} returns dummyUserList[0]

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

        every { userRepository.deleteByEmail(email) } just Runs
    }
}