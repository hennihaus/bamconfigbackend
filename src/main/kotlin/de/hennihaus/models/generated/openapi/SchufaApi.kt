package de.hennihaus.models.generated.openapi

import com.fasterxml.jackson.annotation.JsonProperty

data class SchufaApi(
    val openapi: String,
    val info: SchufaInfo,
    val servers: List<SchufaServer>,
    val paths: SchufaPaths,
    val tags: List<SchufaTag>,
    val components: SchufaComponents,
)

data class SchufaInfo(
    val title: String,
    val contact: SchufaContact,
    val description: String,
    val license: SchufaLicense,
    val version: String,
)

data class SchufaServer(
    val url: String,
    val description: String,
)

data class SchufaPaths(
    @JsonProperty("/rating")
    val rating: SchufaPath,
)

data class SchufaTag(
    val name: String,
)

data class SchufaComponents(
    val parameters: LinkedHashMap<String, SchufaParameter>,
    val responses: LinkedHashMap<String, SchufaResponse>,
    val schemas: SchufaSchemas,
)

data class SchufaContact(
    val name: String,
    val email: String,
)

data class SchufaLicense(
    val name: String,
    val url: String,
)

data class SchufaPath(
    val get: SchufaOperation,
)

data class SchufaOperation(
    val tags: List<String>,
    val operationId: String,
    val parameters: List<SchufaRef>,
    val responses: SchufaResponses,
)

data class SchufaResponses(
    @JsonProperty("200")
    val ok: SchufaRef,
    @JsonProperty("400")
    val badRequest: SchufaRef,
    @JsonProperty("404")
    val notFound: SchufaRef,
    @JsonProperty("500")
    val internalServerError: SchufaRef,
)

data class SchufaSchemas(
    @JsonProperty("RatingLevel")
    val ratingLevel: SchufaRatingLevel,
    @JsonProperty("Rating")
    val rating: SchufaRating,
    @JsonProperty("Error")
    val error: SchufaError,
)

data class SchufaParameter(
    val name: String,
    val description: String,
    @JsonProperty("in")
    val inOption: String,
    val required: Boolean,
    val example: String,
    val schema: SchufaSchema,
)

data class SchufaSchema(
    val type: String?,
    @JsonProperty("\$ref")
    val ref: String?,
    val format: String?,
)

data class SchufaResponse(
    val description: String,
    val content: LinkedHashMap<String, SchufaMediaType>,
)

data class SchufaMediaType(
    val schema: SchufaRef,
    val example: SchufaExample,
)

data class SchufaExample(
    val score: Int?,
    val failureRiskInPercent: Double?,
    val message: String?,
    val dateTime: String?,
)

data class SchufaRatingLevel(
    val type: String,
    val enum: List<String>,
)

data class SchufaRating(
    val type: String,
    val required: List<String>,
    val properties: SchufaRatingProperties,
)

data class SchufaError(
    val type: String,
    val required: List<String>,
    val properties: SchufaErrorProperties,
)

data class SchufaRatingProperties(
    val score: SchufaScore,
    val failureRiskInPercent: SchufaFailureRiskInPercent,
)

data class SchufaScore(
    val type: String,
    val format: String,
    val minimum: Int,
    val maximum: Int,
)

data class SchufaFailureRiskInPercent(
    val type: String,
    val format: String,
    val minimum: Double,
    val maximum: Double,
)

data class SchufaErrorProperties(
    val message: SchufaMessage,
    val dateTime: SchufaDateTime,
)

data class SchufaMessage(
    val type: String,
)

data class SchufaDateTime(
    val type: String,
    val format: String,
)

data class SchufaRef(
    @JsonProperty("\$ref")
    val ref: String,
)
