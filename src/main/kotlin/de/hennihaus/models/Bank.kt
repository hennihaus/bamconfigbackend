package de.hennihaus.models

import de.hennihaus.configurations.MongoConfiguration.ID_FIELD
import de.hennihaus.models.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Bank(
    @SerialName(ID_FIELD)
    val jmsQueue: String,
    val name: String,
    @Serializable(with = UriSerializer::class)
    val thumbnailUrl: URI,
    val isAsync: Boolean,
    val isActive: Boolean,
    val creditConfiguration: CreditConfiguration?,
    val groups: List<Group>,
)

@Serializable
data class CreditConfiguration(
    val minAmountInEuros: Int,
    val maxAmountInEuros: Int,
    val minTermInMonths: Int,
    val maxTermInMonths: Int,
    val minSchufaRating: RatingLevel,
    val maxSchufaRating: RatingLevel,
)

enum class RatingLevel {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    N,
    O,
    P,
}
