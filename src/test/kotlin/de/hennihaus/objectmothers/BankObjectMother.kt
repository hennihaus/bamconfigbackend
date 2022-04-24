package de.hennihaus.objectmothers

import de.hennihaus.models.Bank
import de.hennihaus.models.CreditConfiguration
import de.hennihaus.models.Group
import de.hennihaus.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.GroupObjectMother.getSecondGroup
import de.hennihaus.objectmothers.GroupObjectMother.getThirdGroup

object BankObjectMother {

    private const val DEFAULT_THUMBNAIL_URL = "http://localhost:8085/picture.jpg"
    private const val DEFAULT_IS_ACTIVE = true

    fun getSchufaBank(
        jmsTopic: String = "schufa",
        name: String = "schufa",
        thumbnailUrl: String = DEFAULT_THUMBNAIL_URL,
        isAsync: Boolean = false,
        isActive: Boolean = DEFAULT_IS_ACTIVE,
        creditConfiguration: CreditConfiguration? = null,
        groups: List<Group> = emptyList()
    ) = Bank(
        jmsTopic = jmsTopic,
        name = name,
        thumbnailUrl = thumbnailUrl,
        isAsync = isAsync,
        isActive = isActive,
        creditConfiguration = creditConfiguration,
        groups = groups
    )

    fun getVBank(
        jmsTopic: String = "vbank",
        name: String = "vbank",
        thumbnailUrl: String = "http://localhost:8085/picture.jpg",
        isAsync: Boolean = false,
        isActive: Boolean = DEFAULT_IS_ACTIVE,
        creditConfiguration: CreditConfiguration? = getCreditConfigurationWithNoEmptyFields(),
        groups: List<Group> = emptyList()
    ) = Bank(
        jmsTopic = jmsTopic,
        name = name,
        thumbnailUrl = thumbnailUrl,
        isAsync = isAsync,
        isActive = isActive,
        creditConfiguration = creditConfiguration,
        groups = groups
    )

    fun getJmsBank(
        jmsTopic: String = "jmsBankA",
        name: String = "jmsBankA",
        thumbnailUrl: String = "http://localhost:8085/picture.jpg",
        isAsync: Boolean = true,
        isActive: Boolean = DEFAULT_IS_ACTIVE,
        creditConfiguration: CreditConfiguration? = getCreditConfigurationWithNoEmptyFields(),
        groups: List<Group> = listOf(getFirstGroup(), getSecondGroup(), getThirdGroup())
    ) = Bank(
        jmsTopic = jmsTopic,
        name = name,
        thumbnailUrl = thumbnailUrl,
        isAsync = isAsync,
        isActive = isActive,
        creditConfiguration = creditConfiguration,
        groups = groups
    )
}
