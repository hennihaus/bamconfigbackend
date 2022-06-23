package de.hennihaus.models.generated.openapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchufaApi(
    val openapi: String,
    val info: SchufaInfo,
    val servers: List<SchufaServer>,
    val paths: SchufaPaths,
    val tags: List<SchufaTag>,
    val components: SchufaComponents,
)

@Serializable
data class SchufaInfo(
    val title: String,
    val contact: SchufaContact,
    val description: String,
    val license: SchufaLicense,
    val version: String,
)

@Serializable
data class SchufaServer(
    val url: String,
    val description: String,
)

@Serializable
data class SchufaPaths(
    @SerialName("/rating")
    val rating: SchufaPath,
)

@Serializable
data class SchufaTag(
    val name: String,
)

@Serializable
data class SchufaComponents(
    val parameters: LinkedHashMap<String, SchufaParameter>,
    val responses: LinkedHashMap<String, SchufaResponse>,
    val schemas: SchufaSchemas,
)

@Serializable
data class SchufaContact(
    val name: String,
    val email: String,
)

@Serializable
data class SchufaLicense(
    val name: String,
    val url: String,
)

@Serializable
data class SchufaPath(
    val get: SchufaOperation,
)

@Serializable
data class SchufaOperation(
    val tags: List<String>,
    val operationId: String,
    val parameters: List<SchufaRef>,
    val responses: SchufaResponses,
)

@Serializable
data class SchufaResponses(
    @SerialName("200")
    val ok: SchufaRef,
    @SerialName("400")
    val badRequest: SchufaRef,
    @SerialName("404")
    val notFound: SchufaRef,
    @SerialName("500")
    val internalServerError: SchufaRef,
)

@Serializable
data class SchufaSchemas(
    @SerialName("RatingLevel")
    val ratingLevel: SchufaRatingLevel,
    @SerialName("Rating")
    val rating: SchufaRating,
    @SerialName("Error")
    val error: SchufaError,
)

@Serializable
data class SchufaParameter(
    val name: String,
    val description: String,
    @SerialName("in")
    val inOption: String,
    val required: Boolean,
    val example: String,
    val schema: SchufaSchema,
)

@Serializable
data class SchufaSchema(
    val type: String?,
    @SerialName("\$ref")
    val ref: String?,
    val format: String?,
)

@Serializable
data class SchufaResponse(
    val description: String,
    val content: LinkedHashMap<String, SchufaMediaType>,
)

@Serializable
data class SchufaMediaType(
    val schema: SchufaRef,
    val example: SchufaExample,
)

@Serializable
data class SchufaExample(
    val score: Int?,
    val failureRiskInPercent: Double?,
    val message: String?,
    val dateTime: String?,
)

@Serializable
data class SchufaRatingLevel(
    val type: String,
    val enum: List<String>,
)

@Serializable
data class SchufaRating(
    val type: String,
    val required: List<String>,
    val properties: SchufaRatingProperties,
)

@Serializable
data class SchufaError(
    val type: String,
    val required: List<String>,
    val properties: SchufaErrorProperties,
)

@Serializable
data class SchufaRatingProperties(
    val score: SchufaScore,
    val failureRiskInPercent: SchufaFailureRiskInPercent,
)

@Serializable
data class SchufaScore(
    val type: String,
    val format: String,
    val minimum: Int,
    val maximum: Int,
)

@Serializable
data class SchufaFailureRiskInPercent(
    val type: String,
    val format: String,
    val minimum: Double,
    val maximum: Double,
)

@Serializable
data class SchufaErrorProperties(
    val message: SchufaMessage,
    val dateTime: SchufaDateTime,
)

@Serializable
data class SchufaMessage(
    val type: String,
)

@Serializable
data class SchufaDateTime(
    val type: String,
    val format: String,
)

@Serializable
data class SchufaRef(
    @SerialName("\$ref")
    val ref: String,
)
