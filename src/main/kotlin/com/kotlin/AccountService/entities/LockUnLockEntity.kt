package com.kotlin.AccountService.entities

import com.kotlin.AccountService.entities.enums.LockingCondition

data class LockUnLockEntity (val userEmail: String, val lockingCondition: LockingCondition) {
}

