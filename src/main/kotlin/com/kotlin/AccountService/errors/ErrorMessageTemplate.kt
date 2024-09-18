package com.kotlin.AccountService.errors

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime


@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorMessageTemplate(
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private val timestamp: LocalDateTime,
    private val status: Int,
    private val error: String,
    private val message: String,
    private val path: String
)