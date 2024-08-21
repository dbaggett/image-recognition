package danny.baggett.infrastructure.api

import arrow.core.Either
import danny.baggett.infrastructure.api.model.Error
import danny.baggett.model.error.ImageAnalysisError
import danny.baggett.model.error.ImageError
import danny.baggett.model.error.ImageNotFoundError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun Either<ImageError, Any>.toResponse() = when (this) {
    is Either.Left -> when (this.value) {
        is ImageAnalysisError -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Error(this.value.message))
        is ImageNotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error(this.value.message))
    }
    is Either.Right -> ResponseEntity.status(HttpStatus.OK).body(this.value)
}