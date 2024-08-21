package danny.baggett.infrastructure.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import danny.baggett.infrastructure.api.model.Error
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ControllerErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: Exception, request: WebRequest) = ResponseEntity(
        Error("Bad Request"),
        HttpHeaders(),
        HttpStatus.BAD_REQUEST
    )

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, request: WebRequest): ResponseEntity<Error> {
        val test = ""
        return ResponseEntity(
            Error("Unknown error"),
            HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}