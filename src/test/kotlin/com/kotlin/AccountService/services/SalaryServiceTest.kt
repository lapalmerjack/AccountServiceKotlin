package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.SalaryResponse
import com.kotlin.AccountService.entities.User
import com.kotlin.AccountService.errors.customexceptions.DateSyntaxIncorrectException
import com.kotlin.AccountService.errors.customexceptions.NoExistingDatePeriodException
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
import com.kotlin.AccountService.repositories.SalaryRepository
import com.kotlin.AccountService.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


private val userRepo: UserRepository = mockk()
private val salaryRepository: SalaryRepository = mockk()
private val salaryService: SalaryService = SalaryService(userRepository = userRepo, salaryRepository = salaryRepository)
class SalaryServiceTest {
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
    }

    @Test
    fun `A user is able to retrieve a payment from a certain period` () {
        val email = myUsers[1].email
        every { userRepo.findByEmailIgnoreCase(email)} returns myUsers[1]

       val salaryResponse = salaryService.getPayment(email, "07-2001")

        assertEquals("July-2001", salaryResponse.formattedPeriod)
        assertEquals("8000 dollar(s) 26 cent(s)", salaryResponse.formattedSalary  )

    }

    @Test
    fun `An exception is thrown if the salary period does not exist` () {
        val email = myUsers[1].email
        every { userRepo.findByEmailIgnoreCase(email)} returns myUsers[1]
        val exception = org.junit.jupiter.api.assertThrows<NoExistingDatePeriodException> {
            salaryService.getPayment(email, "02-2027")
        }
        assertEquals("No existing salary period exists for this user", exception.message)
    }

    @Test
    fun `Throws an exception when the syntax for date is wrong` () {

        val exception = org.junit.jupiter.api.assertThrows<DateSyntaxIncorrectException> {
            salaryService.getPayment("cooldude@acme.com", "011-2017")
        }
        assertEquals("Please input the proper date period", exception.message)
    }

    @Test
    fun `returns all payments for a user`() {

        val salaries = salaryService.getPayments("michael@acme.com")

        assertEquals(5, salaries.size)

    }
}