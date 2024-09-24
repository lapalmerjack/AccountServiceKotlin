package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.AuthorityEntity
import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.entities.UserResponse
import com.kotlin.AccountService.errors.customexceptions.*
import com.kotlin.AccountService.repositories.UserRepository
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody


@Service
class AdminService(private val userRepository: UserRepository) {

    private val logger = LoggerFactory.getLogger(AdminService::class.java)

    fun getUsers(): List<User> =  userRepository.findAll()

    fun retrieveEmployeeInfo(email: String): User = userRepository.findByEmailIgnoreCase(email)
        ?: throw UserNotFoundException()

    @Transactional
    fun deleteUserFromDatabase(adminEmail: String, email: String)
    {
        logger.info("Check if admin and user are same")
        if (adminEmail == email) throw AdminCantDeleteItSelfException()

        userRepository.deleteByEmail(email) ?: throw UserNotFoundException()

    }

    @Transactional
    fun updateUserRoles(authorityEntity: AuthorityEntity): UserResponse {
        conductSeriesOfChecks(authorityEntity)
        val user = retrieveEmployeeInfo(authorityEntity.user)
        val userRoles = user.roles.toMutableSet()

        val updatedUserRoles = handleOperationForRoles(userRoles, authorityEntity)
        val updatedUser = user.copy(roles=updatedUserRoles.toMutableSet())

        userRepository.save(updatedUser)

        return UserResponse(id = updatedUser.id!!,
            name = updatedUser.name,
            lastName = updatedUser.lastname,
            email = updatedUser.email,
            roles = updatedUser.roles.toSet() )


    }

    private fun conductSeriesOfChecks(authorityEntity: AuthorityEntity) {
        logger.info("conduct checks before grant/removal of role {}", authorityEntity.role)
        checkRoleName(authorityEntity.role)
    }

    private fun checkRoleName(role: String) {
        val regex = Regex("ADMINISTRATOR|USER|ACCOUNTANT|AUDITOR")

        if (!regex.matches(role)) {
            throw RoleDoesNotExistException()
        }
    }

    private fun handleOperationForRoles(userRoles: MutableSet<Role>, authorityEntity: AuthorityEntity): Set<Role> =
        when (authorityEntity.operation) {
            AuthorityEntity.Operations.GRANT -> grantUserRole(userRoles, authorityEntity.role)
            AuthorityEntity.Operations.REMOVE -> removeUserRole(userRoles, authorityEntity.role)
        }

    private fun removeUserRole(userRoles: MutableSet<Role>, role: String): Set<Role> {
        return emptySet()

    }

    private fun checkIfOneUserAlreadyHasRole(role: String) = userRepository
            .findAll()
            .flatMap { it.roles }
            .map { it.userRole }
            .toSet()
            .contains("ROLE_$role")
            .takeIf { it }
            ?.let { throw RoleAlreadyAssignedException()  }


    private fun grantUserRole(userRoles: MutableSet<Role>, role: String): Set<Role> {
        checkIfOneUserAlreadyHasRole(role)
        userRoles.add(Role(userRole= "ROLE_$role"))
        return  userRoles
    }


}