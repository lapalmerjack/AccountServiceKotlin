package com.kotlin.AccountService.errors

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorMessageTemplate(
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  val timestamp: LocalDateTime,
     val status: Int,
     val error: String,
     val message: String,
    val path: String
)