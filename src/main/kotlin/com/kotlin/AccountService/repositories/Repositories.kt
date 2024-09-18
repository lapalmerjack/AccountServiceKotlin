package com.kotlin.AccountService.repositories

import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.Salary
import com.kotlin.AccountService.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {

    @Query("SELECT s FROM User s WHERE lower(s.email) = lower(?1)")
    fun findByEmailIgnoreCase(email: String): User?

    fun deleteByEmail(email: String): User?

}

@Repository
interface RoleRepository: JpaRepository<Role, Long> {

}

@Repository
interface  SalaryRepository: JpaRepository<Salary, Long> {

}