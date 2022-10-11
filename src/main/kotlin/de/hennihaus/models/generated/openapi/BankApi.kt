package de.hennihaus.models.generated.openapi

import com.fasterxml.jackson.annotation.JsonProperty

data class BankApi(
    val openapi: String,
    val info: BankInfo,
    val servers: List<BankServer>,
    val paths: BankPaths,
    val tags: List<BankTag>,
    val components: BankComponents,
)

data class BankInfo(
    val title: String,
    val contact: BankContact,
    val description: String,
    val license: BankLicense,
    val version: String,
)

data class BankServer(
    val url: String,
    val description: String,
)

data class BankPaths(
    @JsonProperty("/credit")
    val credit: BankPath,
)

data class BankTag(
    val name: String,
)

data class BankComponents(
    val parameters: LinkedHashMap<String, BankParameter>,
    val responses: LinkedHashMap<String, BankResponse>,
    val schemas: BankSchemas,
)

data class BankContact(
    val name: String,
    val email: String,
)

data class BankLicense(
    val name: String,
    val url: String,
)

data class BankPath(
    val get: BankOperation,
)

data class BankOperation(
    val tags: List<String>,
    val operationId: String,
    val parameters: List<BankRef>,
    val responses: BankResponses,
)

data class BankResponses(
    @JsonProperty("200")
    val ok: BankRef,
    @JsonProperty("400")
    val badRequest: BankRef,
    @JsonProperty("404")
    val notFound: BankRef,
    @JsonProperty("500")
    val internalServerError: BankRef,
)

data class BankSchemas(
    @JsonProperty("RatingLevel")
    val ratingLevel: BankRatingLevel,
    @JsonProperty("Credit")
    val credit: BankCredit,
    @JsonProperty("Error")
    val error: BankError,
)

data class BankParameter(
    val name: String,
    val description: String,
    @JsonProperty("in")
    val inOption: String,
    val required: Boolean,
    val example: String,
    val schema: BankSchema,
)

data class BankSchema(
    val type: String?,
    @JsonProperty("\$ref")
    val ref: String?,
    val format: String?,
    val minimum: Int?,
    val maximum: Int?,
)

data class BankResponse(
    val description: String,
    val content: LinkedHashMap<String, BankMediaType>,
)

data class BankMediaType(
    val schema: BankRef,
    val example: BankExample,
)

data class BankExample(
    val lendingRateInPercent: Double?,
    val message: String?,
    val dateTime: String?,
)

data class BankRatingLevel(
    val type: String,
    val enum: List<String>,
)

data class BankCredit(
    val type: String,
    val required: List<String>,
    val properties: BankCreditProperties,
)

data class BankError(
    val type: String,
    val required: List<String>,
    val properties: BankErrorProperties,
)

data class BankCreditProperties(
    val lendingRateInPercent: BankLendingRateInPercent,
)

data class BankLendingRateInPercent(
    val type: String,
    val format: String,
    val minimum: Int,
    val maximum: Int,
)

data class BankErrorProperties(
    val message: BankMessage,
    val dateTime: BankDateTime,
)

data class BankMessage(
    val type: String,
)

data class BankDateTime(
    val type: String,
    val format: String,
)

data class BankRef(
    @JsonProperty("\$ref")
    val ref: String,
)
