package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.AuthorityEntity
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.*
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
        myUsers = dummyUserList.toMutableList()
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

    @Test
    fun `A role can be granted for a user`() {
        val userWithNewRole = myUsers[1]

        val myAuthorityEntity = AuthorityEntity(user = "jane@acme.com", role = "ACCOUNTANT",
            operation = AuthorityEntity.Operations.GRANT)

        every { userRepository.findByEmailIgnoreCase(any()) } returns userWithNewRole
        every { userRepository.save(any()) } answers { firstArg() }
        every { userRepository.findAll()} returns myUsers


        val userResponse = adminService.updateUserRoles(myAuthorityEntity)
        val userRoles = userResponse.roles.map { it.userRole }


        assertTrue(userRoles.contains("ROLE_ACCOUNTANT"))

    }

    @Test
    fun `An error is thrown when the role does not match the four categories`() {
        val myAuthorityEntity = AuthorityEntity(user = "jane@acme.com", role = "BASEBALLGUY",
            operation = AuthorityEntity.Operations.GRANT)
        val exception = assertThrows<RoleDoesNotExistException> {
            adminService.updateUserRoles(myAuthorityEntity)
        }

        assertEquals("Role does not exist", exception.message)

    }

    @Test
    fun `An error is thrown when another user already has the unique role`() {
        val userWithNewRole = myUsers[1]

        val myAuthorityEntity = AuthorityEntity(user = "jane@acme.com", role = "AUDITOR",
            operation = AuthorityEntity.Operations.GRANT)

        every { userRepository.findByEmailIgnoreCase(any()) } returns userWithNewRole
        every { userRepository.findAll()} returns myUsers
        every { userRepository.save(any()) } answers { firstArg() }

        val exception = assertThrows<RoleAlreadyAssignedException> {
            adminService.updateUserRoles(myAuthorityEntity)
        }
        assertEquals("Role already assigned to a user", exception.message)

    }

    @Test
    fun `An error is thrown when the administrator tries to give itself another user a unique role that is already taken`() {
        val userWithNewRole = myUsers[0]

        val myAuthorityEntity = AuthorityEntity(user = "john@acme.com", role = "ACCOUNTANT",
            operation = AuthorityEntity.Operations.GRANT)

        every { userRepository.findByEmailIgnoreCase(any()) } returns userWithNewRole

        val exception = assertThrows<RoleCombinationException> {
            adminService.updateUserRoles(myAuthorityEntity)
        }
        assertEquals("The user cannot combine admin and business roles!", exception.message)
    }

    @Test
    fun `A role for a user is able to be deleted`() {
        val userWithRoleToRemove = myUsers[2]

        val myAuthorityEntity = AuthorityEntity(user = "michael@acme.com", role = "AUDITOR",
            operation = AuthorityEntity.Operations.REMOVE)

        every { userRepository.findByEmailIgnoreCase(any()) } returns userWithRoleToRemove
        every { userRepository.save(any()) } answers { firstArg() }


        val userResponse = adminService.updateUserRoles(myAuthorityEntity)
        val userRoles = userResponse.roles.map { it.userRole }


        assertTrue(userRoles.size == 1)
        assertTrue(!userRoles.contains("ROLE_AUDITOR"))

    }

    @Test
    fun `An error is thrown if administrator try's to delete remove administrator role`() {
        val userWithRoleToRemove = myUsers[2]

        val myAuthorityEntity = AuthorityEntity(user = "john@acme.com", role = "ADMINISTRATOR",
            operation = AuthorityEntity.Operations.REMOVE)

        every { userRepository.findByEmailIgnoreCase(any()) } returns userWithRoleToRemove

        val exception = assertThrows<AdminCantDeleteItSelfException> {
            adminService.updateUserRoles(myAuthorityEntity)
        }

        assertEquals("Can't remove ADMINISTRATOR role!", exception.message)
    }

    @Test
    fun `Throw an error when admin tries to delete last role for a user`() {
        val userWithRoleToRemove = myUsers[3]

        val myAuthorityEntity = AuthorityEntity(user = "john@acme.com", role = "USER",
            operation = AuthorityEntity.Operations.REMOVE)

        every { userRepository.findByEmailIgnoreCase(any()) } returns userWithRoleToRemove

        val exception = assertThrows<InsufficientRoleCountException> {
            adminService.updateUserRoles(myAuthorityEntity)
        }

        assertEquals("The user must have at least one role!", exception.message)


    }


}