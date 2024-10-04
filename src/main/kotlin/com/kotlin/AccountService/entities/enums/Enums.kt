package com.kotlin.AccountService.entities.enums

enum class Enums(val breachedPassword: String) {
    PASSWORD_FOR_JANUARY("PasswordForJanuary"),
    PASSWORD_FOR_FEBRUARY("PasswordForFebruary"),
    PASSWORD_FOR_MARCH("PasswordForMarch"),
    PASSWORD_FOR_APRIL("PasswordForApril"),
    PASSWORD_FOR_MAY("PasswordForMay"),
    PASSWORD_FOR_JUNE("PasswordForJune"),
    PASSWORD_FOR_JULY("PasswordForJuly"),
    PASSWORD_FOR_AUGUST("PasswordForAugust"),
    PASSWORD_FOR_SEPTEMBER("PasswordForSeptember"),
    PASSWORD_FOR_OCTOBER("PasswordForOctober"),
    PASSWORD_FOR_NOVEMBER("PasswordForNovember"),
    PASSWORD_FOR_DECEMBER("PasswordForDecember")
}

enum class LockingCondition { LOCK, UNLOCK }
