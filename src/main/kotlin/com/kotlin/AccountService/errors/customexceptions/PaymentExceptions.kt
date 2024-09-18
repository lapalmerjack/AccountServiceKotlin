package com.kotlin.AccountService.errors.customexceptions

class DateSyntaxIncorrectException(message: String = "Please input the proper date period"):
    RuntimeException(message)

class ExistingDatePeriodException(message: String = "The date period is already entered into database"):
    RuntimeException(message)

class NoExistingDatePeriodException(message: String = "No existing salary period exists for this user"):
    RuntimeException(message)

class SalaryBelowZeroException(message: String = "Salary should not be below zero"): RuntimeException(message)