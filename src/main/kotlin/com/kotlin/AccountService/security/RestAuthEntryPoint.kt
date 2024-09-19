package com.kotlin.AccountService.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver
import java.io.IOException

@Component("delegatedAuthenticationEntryPoint")
class RestAuthEntryPoint : AuthenticationEntryPoint {
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver? = null


    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        println("SUP")
        val noBasicAuthError = "Full authentication is required to access this resource"
        if (authException.message == noBasicAuthError) {
            // Set response status code and content type
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE

            // Create a JSON object for the error response
            val errorResponse = mapOf(
                "status" to HttpServletResponse.SC_UNAUTHORIZED,
                "error" to "Unauthorized",
                "message" to "Authentication failed: ${authException.message}"
            )

            // Convert the error response to JSON and write it to the response body
            ObjectMapper().writeValue(response.writer, errorResponse)
        } else {
            System.out.printf("ERRORS: $authException")
            resolver!!.resolveException(request, response, null, authException)
        }
    }


}