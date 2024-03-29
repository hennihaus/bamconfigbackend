package de.hennihaus.objectmothers

import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.SECOND_TEAM_USERNAME
import de.hennihaus.models.cursors.Direction
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithEmptyFields
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithNoEmptyFields

object CursorObjectMother {

    const val FIRST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACUFTQ0VORElOR3QAAHNyACVkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuVGVhbVF1ZXJ5exKABvaOF6UCAAtJAAVsaW1pdEwABWJhbmtzdAAQTGphdmEvdXRpbC9MaXN0O0wACWhhc1Bhc3NlZHQAE0xqYXZhL2xhbmcvQm9vbGVhbjtMAAhqbXNRdWV1ZXEAfgACTAALbWF4UmVxdWVzdHN0ABBMamF2YS9sYW5nL0xvbmc7TAALbWluUmVxdWVzdHNxAH4ADUwACHBhc3N3b3JkcQB-AAJMABBzdHVkZW50Rmlyc3RuYW1lcQB-AAJMAA9zdHVkZW50TGFzdG5hbWVxAH4AAkwABHR5cGV0ACRMZGUvaGVubmloYXVzL2JhbWRhdGFtb2RlbC9UZWFtVHlwZTtMAAh1c2VybmFtZXEAfgACeHAAAAABc3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAA3QABlNjaHVmYXQADURldXRzY2hlIEJhbmt0AAlTcGFya2Fzc2VzcgARamF2YS5sYW5nLkJvb2xlYW7NIHKA1Zz67gIAAVoABXZhbHVleHAAdAATUmVzcG9uc2VRdWV1ZVRlYW0wMXNyAA5qYXZhLmxhbmcuTG9uZzuL5JDMjyPfAgABSgAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHB__________3NxAH4AGwAAAAAAAAAAdAAKbGtoTnFzdGN4c3QABkFuZ2VsYXQABk1lcmtlbH5yACJkZS5oZW5uaWhhdXMuYmFtZGF0YW1vZGVsLlRlYW1UeXBlAAAAAAAAAAASAAB4cQB-AAZ0AAdSRUdVTEFSdAAGVGVhbTAx"
    const val PREVIOUS_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACkRFU0NFTkRJTkd0AAZUZWFtMDJzcgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLlRlYW1RdWVyeXsSgAb2jhelAgALSQAFbGltaXRMAAViYW5rc3QAEExqYXZhL3V0aWwvTGlzdDtMAAloYXNQYXNzZWR0ABNMamF2YS9sYW5nL0Jvb2xlYW47TAAIam1zUXVldWVxAH4AAkwAC21heFJlcXVlc3RzdAAQTGphdmEvbGFuZy9Mb25nO0wAC21pblJlcXVlc3RzcQB-AA1MAAhwYXNzd29yZHEAfgACTAAQc3R1ZGVudEZpcnN0bmFtZXEAfgACTAAPc3R1ZGVudExhc3RuYW1lcQB-AAJMAAR0eXBldAAkTGRlL2hlbm5paGF1cy9iYW1kYXRhbW9kZWwvVGVhbVR5cGU7TAAIdXNlcm5hbWVxAH4AAnhwAAAAAXNyABpqYXZhLnV0aWwuQXJyYXlzJEFycmF5TGlzdNmkPL7NiAbSAgABWwABYXQAE1tMamF2YS9sYW5nL09iamVjdDt4cHVyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5-kde0cCAAB4cAAAAAN0AAZTY2h1ZmF0AA1EZXV0c2NoZSBCYW5rdAAJU3Bhcmthc3Nlc3IAEWphdmEubGFuZy5Cb29sZWFuzSBygNWc-u4CAAFaAAV2YWx1ZXhwAHQAE1Jlc3BvbnNlUXVldWVUZWFtMDFzcgAOamF2YS5sYW5nLkxvbmc7i-SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwf_________9zcQB-ABsAAAAAAAAAAHQACmxraE5xc3RjeHN0AAZBbmdlbGF0AAZNZXJrZWx-cgAiZGUuaGVubmloYXVzLmJhbWRhdGFtb2RlbC5UZWFtVHlwZQAAAAAAAAAAEgAAeHEAfgAGdAAHUkVHVUxBUnQABlRlYW0wMQ=="
    const val NEXT_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACUFTQ0VORElOR3QABlRlYW0wMnNyACVkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuVGVhbVF1ZXJ5exKABvaOF6UCAAtJAAVsaW1pdEwABWJhbmtzdAAQTGphdmEvdXRpbC9MaXN0O0wACWhhc1Bhc3NlZHQAE0xqYXZhL2xhbmcvQm9vbGVhbjtMAAhqbXNRdWV1ZXEAfgACTAALbWF4UmVxdWVzdHN0ABBMamF2YS9sYW5nL0xvbmc7TAALbWluUmVxdWVzdHNxAH4ADUwACHBhc3N3b3JkcQB-AAJMABBzdHVkZW50Rmlyc3RuYW1lcQB-AAJMAA9zdHVkZW50TGFzdG5hbWVxAH4AAkwABHR5cGV0ACRMZGUvaGVubmloYXVzL2JhbWRhdGFtb2RlbC9UZWFtVHlwZTtMAAh1c2VybmFtZXEAfgACeHAAAAABc3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAA3QABlNjaHVmYXQADURldXRzY2hlIEJhbmt0AAlTcGFya2Fzc2VzcgARamF2YS5sYW5nLkJvb2xlYW7NIHKA1Zz67gIAAVoABXZhbHVleHAAdAATUmVzcG9uc2VRdWV1ZVRlYW0wMXNyAA5qYXZhLmxhbmcuTG9uZzuL5JDMjyPfAgABSgAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHB__________3NxAH4AGwAAAAAAAAAAdAAKbGtoTnFzdGN4c3QABkFuZ2VsYXQABk1lcmtlbH5yACJkZS5oZW5uaWhhdXMuYmFtZGF0YW1vZGVsLlRlYW1UeXBlAAAAAAAAAAASAAB4cQB-AAZ0AAdSRUdVTEFSdAAGVGVhbTAx"
    const val LAST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACkRFU0NFTkRJTkd0AABzcgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLlRlYW1RdWVyeXsSgAb2jhelAgALSQAFbGltaXRMAAViYW5rc3QAEExqYXZhL3V0aWwvTGlzdDtMAAloYXNQYXNzZWR0ABNMamF2YS9sYW5nL0Jvb2xlYW47TAAIam1zUXVldWVxAH4AAkwAC21heFJlcXVlc3RzdAAQTGphdmEvbGFuZy9Mb25nO0wAC21pblJlcXVlc3RzcQB-AA1MAAhwYXNzd29yZHEAfgACTAAQc3R1ZGVudEZpcnN0bmFtZXEAfgACTAAPc3R1ZGVudExhc3RuYW1lcQB-AAJMAAR0eXBldAAkTGRlL2hlbm5paGF1cy9iYW1kYXRhbW9kZWwvVGVhbVR5cGU7TAAIdXNlcm5hbWVxAH4AAnhwAAAAAXNyABpqYXZhLnV0aWwuQXJyYXlzJEFycmF5TGlzdNmkPL7NiAbSAgABWwABYXQAE1tMamF2YS9sYW5nL09iamVjdDt4cHVyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5-kde0cCAAB4cAAAAAN0AAZTY2h1ZmF0AA1EZXV0c2NoZSBCYW5rdAAJU3Bhcmthc3Nlc3IAEWphdmEubGFuZy5Cb29sZWFuzSBygNWc-u4CAAFaAAV2YWx1ZXhwAHQAE1Jlc3BvbnNlUXVldWVUZWFtMDFzcgAOamF2YS5sYW5nLkxvbmc7i-SQzI8j3wIAAUoABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwf_________9zcQB-ABsAAAAAAAAAAHQACmxraE5xc3RjeHN0AAZBbmdlbGF0AAZNZXJrZWx-cgAiZGUuaGVubmloYXVzLmJhbWRhdGFtb2RlbC5UZWFtVHlwZQAAAAAAAAAAEgAAeHEAfgAGdAAHUkVHVUxBUnQABlRlYW0wMQ=="
    const val FIRST_TEAM_CURSOR_WITH_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACUFTQ0VORElOR3QAAHNyACVkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuVGVhbVF1ZXJ5exKABvaOF6UCAAtJAAVsaW1pdEwABWJhbmtzdAAQTGphdmEvdXRpbC9MaXN0O0wACWhhc1Bhc3NlZHQAE0xqYXZhL2xhbmcvQm9vbGVhbjtMAAhqbXNRdWV1ZXEAfgACTAALbWF4UmVxdWVzdHN0ABBMamF2YS9sYW5nL0xvbmc7TAALbWluUmVxdWVzdHNxAH4ADUwACHBhc3N3b3JkcQB-AAJMABBzdHVkZW50Rmlyc3RuYW1lcQB-AAJMAA9zdHVkZW50TGFzdG5hbWVxAH4AAkwABHR5cGV0ACRMZGUvaGVubmloYXVzL2JhbWRhdGFtb2RlbC9UZWFtVHlwZTtMAAh1c2VybmFtZXEAfgACeHAAAAABcHBwcHBwcHBwcA=="
    const val PREVIOUS_TEAM_CURSOR_WITH_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACkRFU0NFTkRJTkd0AAZUZWFtMDJzcgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLlRlYW1RdWVyeXsSgAb2jhelAgALSQAFbGltaXRMAAViYW5rc3QAEExqYXZhL3V0aWwvTGlzdDtMAAloYXNQYXNzZWR0ABNMamF2YS9sYW5nL0Jvb2xlYW47TAAIam1zUXVldWVxAH4AAkwAC21heFJlcXVlc3RzdAAQTGphdmEvbGFuZy9Mb25nO0wAC21pblJlcXVlc3RzcQB-AA1MAAhwYXNzd29yZHEAfgACTAAQc3R1ZGVudEZpcnN0bmFtZXEAfgACTAAPc3R1ZGVudExhc3RuYW1lcQB-AAJMAAR0eXBldAAkTGRlL2hlbm5paGF1cy9iYW1kYXRhbW9kZWwvVGVhbVR5cGU7TAAIdXNlcm5hbWVxAH4AAnhwAAAAAXBwcHBwcHBwcHA="
    const val NEXT_TEAM_CURSOR_WITH_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACUFTQ0VORElOR3QABlRlYW0wMnNyACVkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuVGVhbVF1ZXJ5exKABvaOF6UCAAtJAAVsaW1pdEwABWJhbmtzdAAQTGphdmEvdXRpbC9MaXN0O0wACWhhc1Bhc3NlZHQAE0xqYXZhL2xhbmcvQm9vbGVhbjtMAAhqbXNRdWV1ZXEAfgACTAALbWF4UmVxdWVzdHN0ABBMamF2YS9sYW5nL0xvbmc7TAALbWluUmVxdWVzdHNxAH4ADUwACHBhc3N3b3JkcQB-AAJMABBzdHVkZW50Rmlyc3RuYW1lcQB-AAJMAA9zdHVkZW50TGFzdG5hbWVxAH4AAkwABHR5cGV0ACRMZGUvaGVubmloYXVzL2JhbWRhdGFtb2RlbC9UZWFtVHlwZTtMAAh1c2VybmFtZXEAfgACeHAAAAABcHBwcHBwcHBwcA=="
    const val LAST_TEAM_CURSOR_WITH_EMPTY_FIELDS = "rO0ABXNyACJkZS5oZW5uaWhhdXMubW9kZWxzLmN1cnNvcnMuQ3Vyc29yTeaZXTB0yLYCAANMAAlkaXJlY3Rpb250ACdMZGUvaGVubmloYXVzL21vZGVscy9jdXJzb3JzL0RpcmVjdGlvbjtMAAhwb3NpdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXF1ZXJ5dAASTGphdmEvbGFuZy9PYmplY3Q7eHB-cgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLkRpcmVjdGlvbgAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQACkRFU0NFTkRJTkd0AABzcgAlZGUuaGVubmloYXVzLm1vZGVscy5jdXJzb3JzLlRlYW1RdWVyeXsSgAb2jhelAgALSQAFbGltaXRMAAViYW5rc3QAEExqYXZhL3V0aWwvTGlzdDtMAAloYXNQYXNzZWR0ABNMamF2YS9sYW5nL0Jvb2xlYW47TAAIam1zUXVldWVxAH4AAkwAC21heFJlcXVlc3RzdAAQTGphdmEvbGFuZy9Mb25nO0wAC21pblJlcXVlc3RzcQB-AA1MAAhwYXNzd29yZHEAfgACTAAQc3R1ZGVudEZpcnN0bmFtZXEAfgACTAAPc3R1ZGVudExhc3RuYW1lcQB-AAJMAAR0eXBldAAkTGRlL2hlbm5paGF1cy9iYW1kYXRhbW9kZWwvVGVhbVR5cGU7TAAIdXNlcm5hbWVxAH4AAnhwAAAAAXBwcHBwcHBwcHA="

    const val ASCENDING_DIRECTION = "ASCENDING"
    const val DESCENDING_DIRECTION = "DESCENDING"

    const val EMPTY_CURSOR = ""

    fun getFirstTeamCursorWithNoEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getFirstTeamCursorWithEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getPreviousTeamCursorWithNoEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getPreviousTeamCursorWithEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getNextTeamCursorWithNoEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getNextTeamCursorWithEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getLastTeamCursorWithNoEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getLastTeamCursorWithEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )
}
