package de.hennihaus.objectmothers

import de.hennihaus.models.generated.Student
import java.util.UUID

object StudentObjectMother {

    const val FIRST_STUDENT_UUID = "474e90cb-de6e-47f2-844a-44ea7dc09516"
    const val SECOND_STUDENT_UUID = "31b1631b-66c9-4720-94fc-95891a38f964"
    const val THIRD_STUDENT_UUID = "f334a0c0-0c26-4499-aa95-061546839a75"

    fun getFirstStudent(
        uuid: UUID = UUID.fromString(FIRST_STUDENT_UUID),
        firstname: String = "Angela",
        lastname: String = "Merkel",
    ) = Student(
        uuid = uuid,
        firstname = firstname,
        lastname = lastname,
    )

    fun getSecondStudent(
        uuid: UUID = UUID.fromString(SECOND_STUDENT_UUID),
        firstname: String = "Max",
        lastname: String = "Mustermann",
    ) = Student(
        uuid = uuid,
        firstname = firstname,
        lastname = lastname,
    )

    fun getThirdStudent(
        uuid: UUID = UUID.fromString(THIRD_STUDENT_UUID),
        firstname: String = "Thomas",
        lastname: String = "Müller",
    ) = Student(
        uuid = uuid,
        firstname = firstname,
        lastname = lastname,
    )
}
