package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
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
    fun deleteUserFromDatabase(adminEmail: String, email: String) {

    }

}