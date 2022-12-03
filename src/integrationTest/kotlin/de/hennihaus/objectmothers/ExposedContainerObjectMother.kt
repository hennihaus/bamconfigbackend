package de.hennihaus.objectmothers

import java.util.UUID

object ExposedContainerObjectMother {

    val UNKNOWN_UUID: UUID = UUID.fromString("3cdfd9da-e36f-4d2d-9cdc-ec5c78a29b87")
    val TASK_UUID: UUID = UUID.fromString("4ff1f9cb-e65d-4c8f-908a-d036700b757e")
    val BANK_UUID: UUID = UUID.fromString("38be8cac-34c0-4508-93a9-3b570a00e41e")
    val TEAM_UUID: UUID = UUID.fromString("cfbec9fc-323d-4114-8330-a25d3ab8b218")
    val STUDENT_UUID: UUID = UUID.fromString("37ab108f-b97a-4e03-a318-054311438125")

    const val STUDENT_FIRSTNAME = "Jayden"
    const val STUDENT_LASTNAME = "Welsch"

    const val TEAM_USERNAME = "Team000001"
    const val TEAM_PASSWORD = "DodwtsHWHA"
    const val TEAM_JMS_QUEUE = "ResponseQueueTeam000001"

    const val BANK_NAME = "PSD Bank"
    const val BANK_SYNC_COUNT = 2L
    const val BANK_ASYNC_COUNT = 10L

    const val TASK_TITLE = "Asynchrone Banken"

    const val PARAMETER_NAME = "username"
}
