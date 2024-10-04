package com.kotlin.AccountService.services

 inline fun checkCondition(condition: () -> Boolean, exception: RuntimeException) =
    if (condition()) throw exception else Unit