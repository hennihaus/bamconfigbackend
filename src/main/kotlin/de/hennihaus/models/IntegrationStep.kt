package de.hennihaus.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IntegrationStep(val value: Int) {
    @SerialName(value = "1")
    SCHUFA_STEP(value = 1),

    @SerialName(value = "2")
    SYNC_BANK_STEP(value = 2),

    @SerialName(value = "3")
    ASYNC_BANK_STEP(value = 3),
}
