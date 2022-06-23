package de.hennihaus.models.generated.openapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BankApi(
    val openapi: String,
    val info: BankInfo,
    val servers: List<BankServer>,
    val paths: BankPaths,
    val tags: List<BankTag>,
    val components: BankComponents,
)

@Serializable
data class BankInfo(
    val title: String,
    val contact: BankContact,
    val description: String,
    val license: BankLicense,
    val version: String,
)

@Serializable
data class BankServer(
    val url: String,
    val description: String,
)

@Serializable
data class BankPaths(
    @SerialName("/credit")
    val credit: BankPath,
)

@Serializable
data class BankTag(
    val name: String,
)

@Serializable
data class BankComponents(
    val parameters: LinkedHashMap<String, BankParameter>,
    val responses: LinkedHashMap<String, BankResponse>,
    val schemas: BankSchemas,
)

@Serializable
data class BankContact(
    val name: String,
    val email: String,
)

@Serializable
data class BankLicense(
    val name: String,
    val url: String,
)

@Serializable
data class BankPath(
    val get: BankOperation,
)

@Serializable
data class BankOperation(
    val tags: List<String>,
    val operationId: String,
    val parameters: List<BankRef>,
    val responses: BankResponses,
)

@Serializable
data class BankResponses(
    @SerialName("200")
    val ok: BankRef,
    @SerialName("400")
    val badRequest: BankRef,
    @SerialName("404")
    val notFound: BankRef,
    @SerialName("500")
    val internalServerError: BankRef,
)

@Serializable
data class BankSchemas(
    @SerialName("RatingLevel")
    val ratingLevel: BankRatingLevel,
    @SerialName("Credit")
    val credit: BankCredit,
    @SerialName("Error")
    val error: BankError,
)

@Serializable
data class BankParameter(
    val name: String,
    val description: String,
    @SerialName("in")
    val inOption: String,
    val required: Boolean,
    val example: String,
    val schema: BankSchema,
)

@Serializable
data class BankSchema(
    val type: String?,
    @SerialName("\$ref")
    val ref: String?,
    val format: String?,
    val minimum: Int?,
    val maximum: Int?,
)

@Serializable
data class BankResponse(
    val description: String,
    val content: LinkedHashMap<String, BankMediaType>,
)

@Serializable
data class BankMediaType(
    val schema: BankRef,
    val example: BankExample,
)

@Serializable
data class BankExample(
    val lendingRateInPercent: Double?,
    val message: String?,
    val dateTime: String?,
)

@Serializable
data class BankRatingLevel(
    val type: String,
    val enum: List<String>,
)

@Serializable
data class BankCredit(
    val type: String,
    val required: List<String>,
    val properties: BankCreditProperties,
)

@Serializable
data class BankError(
    val type: String,
    val required: List<String>,
    val properties: BankErrorProperties,
)

@Serializable
data class BankCreditProperties(
    val lendingRateInPercent: BankLendingRateInPercent,
)

@Serializable
data class BankLendingRateInPercent(
    val type: String,
    val format: String,
    val minimum: Int,
    val maximum: Int,
)

@Serializable
data class BankErrorProperties(
    val message: BankMessage,
    val dateTime: BankDateTime,
)

@Serializable
data class BankMessage(
    val type: String,
)

@Serializable
data class BankDateTime(
    val type: String,
    val format: String,
)

@Serializable
data class BankRef(
    @SerialName("\$ref")
    val ref: String,
)
