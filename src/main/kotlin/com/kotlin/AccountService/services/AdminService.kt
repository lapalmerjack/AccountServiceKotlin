package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.*
import com.kotlin.AccountService.entities.enums.LockingCondition
import com.kotlin.AccountService.errors.customexceptions.*
import com.kotlin.AccountService.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


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
        throwErrorIfRoleDoesNotExist(authorityEntity.role)
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

    @Transactional
    fun updateUserLockCondition(lockUnLockEntity: LockUnLockEntity): User {

        val user = userRepository.findByEmailIgnoreCase(lockUnLockEntity.userEmail)
            ?: throw UserNotFoundException()

        val updatedUserLock = when(lockUnLockEntity.lockingCondition) {
            LockingCondition.LOCK -> lockUser(user)
            LockingCondition.UNLOCK -> unlockUser(user)
        }


        return updatedUserLock
    }

    private fun unlockUser(user: User): User {
        return user.copy(isAccountUnLocked = true, loginAttempts = 0)

    }

    private fun lockUser(user: User): User {
        checkCondition(
            condition = { user.roles.map { it.userRole }.contains("ROLE_ADMINISTRATOR") },
            exception = CanNotLockAdministratorException()
        )
        return user.copy(isAccountUnLocked = false)

    }

    private fun throwErrorIfRoleDoesNotExist(role: String) =
        checkCondition(
            condition = { !Regex("ADMINISTRATOR|USER|ACCOUNTANT|AUDITOR").matches(role)},
            exception = RoleDoesNotExistException()
        )


    private fun handleOperationForRoles(userRoles: MutableSet<Role>, authorityEntity: AuthorityEntity): Set<Role> =
        when (authorityEntity.operation) {
            AuthorityEntity.Operations.GRANT -> grantUserRole(userRoles, authorityEntity.role)
            AuthorityEntity.Operations.REMOVE -> removeUserRole(userRoles, authorityEntity.role)
        }

    private fun removeUserRole(userRoles: MutableSet<Role>, role: String): Set<Role> {
        throwErrorIfRoleToRemoveIsAdmin(role)
        throwErrorIfOnlyOneRoleExistAndCantBeRemoved(userRoles)

        return userRoles. filter { it.userRole != "ROLE_$role" }.toSet()


    }

    private fun throwErrorIfOnlyOneRoleExistAndCantBeRemoved(userRoles: MutableSet<Role>) =
        checkCondition(
            condition = { userRoles.size <= 1 },
            exception = InsufficientRoleCountException()
        )

    private fun throwErrorIfRoleToRemoveIsAdmin(role: String) =
        checkCondition(
            condition = { role == "ADMINISTRATOR"},
            exception = AdminCantDeleteItSelfException()
        )


    private fun checkIfOneUserAlreadyHasRole(role: String) =
        checkCondition(
            condition = { userRepository.findAll()
                .flatMap { it.roles }
                .any { it.userRole == "ROLE_$role"  }},
            exception =  RoleAlreadyAssignedException()
        )




    private fun grantUserRole(userRoles: MutableSet<Role>, role: String): Set<Role> {
        throwErrorIfGranteeIsAdmin(userRoles)
        checkIfOneUserAlreadyHasRole(role)
        userRoles.add(Role(userRole= "ROLE_$role"))
        return  userRoles
    }

    private fun throwErrorIfGranteeIsAdmin(userRoles: MutableSet<Role>) =
        checkCondition(
            condition = { userRoles.any { it.userRole == "ROLE_ADMINISTRATOR"} },
            exception = RoleCombinationException()
        )
//        userRoles.any { it.userRole == "ROLE_ADMINISTRATOR" }.takeIf { it }
//            ?.let { throw RoleCombinationException() }
}