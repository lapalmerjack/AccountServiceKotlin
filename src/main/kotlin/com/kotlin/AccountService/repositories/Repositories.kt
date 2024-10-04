package com.kotlin.AccountService.repositories

import com.kotlin.AccountService.entities.Role
import com.kotlin.AccountService.entities.Salary
import com.kotlin.AccountService.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, Long> {

    @Query("SELECT s FROM User s WHERE lower(s.email) = lower(?1)")
    fun findByEmailIgnoreCase(email: String): User?

    fun deleteByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

}

@Repository
interface RoleRepository: JpaRepository<Role, Long> {

}

@Repository
interface  SalaryRepository: JpaRepository<Salary, Long> {

    @Query("Select s FROM Salary s WHERE s.employee = ?1 AND s.period =?2")
    fun findSalaryByEmailAndPeriod(email: String, period: String): Salary?


}