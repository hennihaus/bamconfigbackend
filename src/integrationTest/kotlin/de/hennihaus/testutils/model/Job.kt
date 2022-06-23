package de.hennihaus.testutils.model

import de.hennihaus.objectmothers.BrokerContainerObjectMother.DEFAULT_TEST_DELAY

data class Job(val message: String, val delayInMilliseconds: Long = DEFAULT_TEST_DELAY)
