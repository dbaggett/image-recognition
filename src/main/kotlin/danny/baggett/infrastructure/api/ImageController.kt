package danny.baggett.infrastructure.api

import danny.baggett.infrastructure.api.model.ImageSubmissionRequest
import danny.baggett.infrastructure.api.model.toImageResponse
import danny.baggett.model.service.MetaService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/images")
class ImageController(private val metaService: MetaService) {

    @PostMapping
    suspend fun submitImage(@Valid @RequestBody imageSubmissionRequest: ImageSubmissionRequest) = metaService
        .analyzeAndSave(imageSubmissionRequest.toPreAnalyzedImage())
        .map { it.toImageResponse() }
        .toResponse()

    @GetMapping
    suspend fun retrieveImages(@RequestParam(required = false) objects: List<String>?) = metaService
        .retrieveImages(objects ?: emptyList())
        .map { images -> images.map { image -> image.toImageResponse() } }
        .toResponse()

    @GetMapping("/{imageId}")
    suspend fun retrieveImage(@PathVariable imageId: Long) = metaService
        .retrieveSingleImage(imageId)
        .map { it.toImageResponse() }
        .toResponse()
}