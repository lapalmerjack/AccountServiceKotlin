package com.kotlin.AccountService.services

import com.kotlin.AccountService.entities.Salary
import com.kotlin.AccountService.entities.SalaryResponse
import com.kotlin.AccountService.errors.customexceptions.DateSyntaxIncorrectException
import com.kotlin.AccountService.errors.customexceptions.NoExistingDatePeriodException
import com.kotlin.AccountService.errors.customexceptions.UserNotFoundException
import com.kotlin.AccountService.repositories.SalaryRepository
import com.kotlin.AccountService.repositories.UserRepository
import org.springframework.stereotype.Service
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Service
class SalaryService(private val salaryRepository: SalaryRepository, private val userRepository: UserRepository) {


    fun getPayment(userEmail: String, period: String): SalaryResponse {
        println(period)
        checkPeriodSyntax(period)
        val fetchedUser = userRepository.findByEmailIgnoreCase(userEmail)
            ?: throw UserNotFoundException()

        val salary = fetchedUser.salaries.find { it.period == period } ?:
        throw NoExistingDatePeriodException()

        val formattedSalary = formatSalaryResponse(salary.salary)
        val formatPeriodResponse = formatPeriodResponse(period)

        return SalaryResponse(fetchedUser.name, fetchedUser.lastname, formatPeriodResponse, formattedSalary)

    }

    fun getPayments(userEmail: String): List<SalaryResponse> {
        val user = userRepository.findByEmailIgnoreCase(userEmail) ?: throw UserNotFoundException()

        return emptyList()
    }
    private fun checkPeriodSyntax(date: String) =
        checkCondition(
            condition = { !Regex("^(0[1-9]|1[0-2])-(20\\d{2})$").matches(date) },
            exception = DateSyntaxIncorrectException()
        )

    private fun formatSalaryResponse(salary: Long): String =
        String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100)

    private fun formatPeriodResponse(period: String): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy")
        val date = YearMonth.parse(period, dateTimeFormatter)
        val monthName = date.month.toString().lowercase().replaceFirstChar { it.uppercase() }
        return "$monthName-${date.year}"
    }
}