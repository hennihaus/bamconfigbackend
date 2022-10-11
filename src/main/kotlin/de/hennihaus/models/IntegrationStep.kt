package de.hennihaus.models

enum class IntegrationStep(val value: Int) {
    SCHUFA_STEP(value = 1),

    SYNC_BANK_STEP(value = 2),

    ASYNC_BANK_STEP(value = 3),
}
