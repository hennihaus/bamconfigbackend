package de.hennihaus.objectmothers

import java.util.UUID

object ExposedContainerObjectMother {

    val UNKNOWN_UUID: UUID = UUID.fromString("3cdfd9da-e36f-4d2d-9cdc-ec5c78a29b87")
    val TASK_UUID: UUID = UUID.fromString("4ff1f9cb-e65d-4c8f-908a-d036700b757e")
    val BANK_UUID: UUID = UUID.fromString("78eefcd4-2459-412e-857f-60f7c5d531c9")
    val TEAM_UUID: UUID = UUID.fromString("63701a57-91b8-40d4-b4e2-b8dd9e7a75a5")
    val STUDENT_UUID: UUID = UUID.fromString("6a310db6-7d78-4219-8f0f-358afc9c2083")

    const val TEAM_USERNAME = "Team01"
    const val TEAM_PASSWORD = "lkhNqstcxs"
    const val TEAM_JMS_QUEUE = "ResponseLoanBrokerTeam01"

    const val PSD_BANK_NAME = "PSD Bank"

    const val TASK_TITLE = "Asynchrone Banken"
}
