package de.hennihaus.objectmothers

import java.util.UUID

object ExposedContainerObjectMother {

    val UNKNOWN_UUID: UUID = UUID.fromString("3cdfd9da-e36f-4d2d-9cdc-ec5c78a29b87")
    val TASK_UUID: UUID = UUID.fromString("4ff1f9cb-e65d-4c8f-908a-d036700b757e")
    val BANK_UUID: UUID = UUID.fromString("78eefcd4-2459-412e-857f-60f7c5d531c9")
    val TEAM_UUID: UUID = UUID.fromString("ad5181da-d8ed-45e6-ae09-a7a775ea55c1")
    val STUDENT_UUID: UUID = UUID.fromString("8c2d8e9d-fd9a-43b8-96e0-85f2e72aea5c")

    const val STUDENT_FIRSTNAME = "Corinna"
    const val STUDENT_LASTNAME = "Wurm"

    const val TEAM_USERNAME = "Team000001"
    const val TEAM_PASSWORD = "sPSwsUwPOQ"
    const val TEAM_JMS_QUEUE = "ResponseQueueTeam000001"

    const val PSD_BANK_NAME = "PSD Bank"

    const val TASK_TITLE = "Asynchrone Banken"

    const val PARAMETER_NAME = "username"
}
