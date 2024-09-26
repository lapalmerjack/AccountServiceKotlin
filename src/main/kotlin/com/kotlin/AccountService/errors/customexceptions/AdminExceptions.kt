package com.kotlin.AccountService.errors.customexceptions



class CanNotLockAdministratorException(message: String = "Can't lock the Administrator!"): RuntimeException(message)

class InsufficientRoleCountException(message: String = "The user must have at least one role!"): RuntimeException(message)

class OperationDoesNotExistException(message: String = "This operation does not exist"): RuntimeException(message)

class RoleAlreadyAssignedException(message: String = "Role already assigned to a user"): RuntimeException(message)


class RoleCombinationException(message: String = "The user cannot combine admin and business roles!"): RuntimeException(message)


class RoleDoesNotExistException(message: String = "Role does not exist"): RuntimeException(message)

class RoleDoesNotExistForUser(message: String = "Role does not exist for user"): RuntimeException(message)

class AdminCantDeleteItSelfException(message: String = "Can't remove ADMINISTRATOR role!"): RuntimeException(message)

class UserRoleExistsException(message: String = "User already has this role"): RuntimeException(message)

