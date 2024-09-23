package com.kotlin.AccountService.errors

import com.kotlin.AccountService.errors.customexceptions.*
import lombok.extern.java.Log


import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    private val LOGGER = LoggerFactory.getLogger(ResponseEntityExceptionHandler::class.java)
    
    @ExceptionHandler(
        UsernameNotFoundException::class,
        BadCredentialsException::class,
        AuthenticationException::class,
        LockedException::class
    )
    @ResponseBody
    fun handleUnauthenticatedException(
        webRequest: WebRequest,
        e: RuntimeException
    ): ResponseEntity<ErrorMessageTemplate> {
        LOGGER.info("preparing bad credentials for authentication")

        val errorMessage = setUpErrorMessageTemplate(
            e.message, HttpStatus.UNAUTHORIZED.value(),
            webRequest, "Unauthorized"
        )


        return ResponseEntity(errorMessage, HttpStatus.UNAUTHORIZED)
    }


    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders, status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        LOGGER.error("Preparing message not readable Exception")

        if (ex.message!!.contains("[GRANT, REMOVE]")) {
            LOGGER.error("Preparing role does not exist exception")
            val operationDoesNotExistException = OperationDoesNotExistException()
            val errorMessage = setUpErrorMessageTemplate(
                operationDoesNotExistException.message, HttpStatus.BAD_REQUEST.value(),
                request, "Bad Request"
            )

            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request)
    }


    @ExceptionHandler(
        NewPasswordMatchesOldPasswordException::class,
        UserFoundException::class,
        PasswordMatchesBannedPasswordException::class,
        MinimumPasswordLengthException::class,
        DateSyntaxIncorrectException::class,
        ExistingDatePeriodException::class,
        SalaryBelowZeroException::class,
        NoExistingDatePeriodException::class,
        UserRoleExistsException::class,
        AdminCantDeleteItSelfException::class,
        RoleCombinationException::class,
        RoleAlreadyAssignedException::class,
        InsufficientRoleCountException::class,
        RoleDoesNotExistForUser::class,
        CanNotLockAdministratorException::class
    )
    fun HandlingServiceRuntimeExceptions(
        e: RuntimeException, webRequest: WebRequest
    ): ResponseEntity<ErrorMessageTemplate> {
        LOGGER.info("Preparing Bad Request Exception")
        LOGGER.error(e.cause.toString() + ": " + e.message)

        val errorMessage = setUpErrorMessageTemplate(
            e.message, HttpStatus.BAD_REQUEST.value(),
            webRequest, "Bad Request"
        )

        LOGGER.info("${errorMessage.toString()} is my error message")

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        OperationDoesNotExistException::class, UserNotFoundException::class, RoleDoesNotExistException::class
    )
    fun handleUserNotFoundException(
        e: RuntimeException, webRequest: WebRequest
    ): ResponseEntity<ErrorMessageTemplate> {
        LOGGER.info("Preparing exception")
        LOGGER.error(e.cause.toString() + ": " + e.message)

        val errorMessage = setUpErrorMessageTemplate(
            e.message, HttpStatus.NOT_FOUND.value(),
            webRequest, "Not Found"
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }


    private fun processFieldErrors(fieldErrors: List<FieldError>): String? {
        var errorMessage: String? = null
        for (fieldError in fieldErrors) {
            errorMessage = fieldError.defaultMessage
        }
        return errorMessage
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val result = ex.bindingResult
        val fieldErrors = result.fieldErrors
        val errorMessageString = processFieldErrors(fieldErrors)

        LOGGER.info("Handling methodArgumentNotValid")

        val errorMessage = setUpErrorMessageTemplate(
            errorMessageString,
            HttpStatus.BAD_REQUEST.value(), request, "Bad Request"
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    private fun setUpErrorMessageTemplate(
        errorMessage: String?, httpValue: Int,
        webRequest: WebRequest, error: String
    ): ErrorMessageTemplate {
        val urlPath = getUrlPath(webRequest)

        return ErrorMessageTemplate(
            LocalDateTime.now(),
            httpValue,
            error,
            errorMessage!!,
            urlPath
        )
    }

    private fun getUrlPath(path: WebRequest): String {
        LOGGER.info("providing the url path")
        val urlPath = path.getDescription(false)

        return urlPath.substring(urlPath.indexOf("/"))
    }
    

}