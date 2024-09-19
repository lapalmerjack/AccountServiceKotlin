package com.kotlin.AccountService.security.customconfig

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import java.io.IOException
import java.time.LocalDateTime


class CustomAccessDeniedHandler @Autowired constructor(private val objectMapper: ObjectMapper) : AccessDeniedHandler {
    @Throws(IOException::class, ServletException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = "application/json"


        val data = linkedMapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpStatus.FORBIDDEN.value(),
            "error" to HttpStatus.FORBIDDEN.reasonPhrase,
            "message" to "Access Denied!",
            "path" to request.requestURI
        )

        response.outputStream.println(ObjectMapper().writeValueAsString(data))
    }


    private val username: String?
        get() {
            // Retrieve the authentication object from the SecurityContextHolder
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication != null && authentication.isAuthenticated) {
                // Retrieve the username from the authentication object
                return authentication.name
            }

            // If the authentication object is not available or not authenticated, return null or handle accordingly
            return null
        }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CustomAccessDeniedHandler::class.java)
    }
}
