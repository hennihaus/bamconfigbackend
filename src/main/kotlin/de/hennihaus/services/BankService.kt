package de.hennihaus.services

import de.hennihaus.models.Bank

interface BankService {
    suspend fun getAllBanks(): List<Bank>
    suspend fun getBankByJmsQueue(jmsQueue: String): Bank
    suspend fun saveBank(bank: Bank): Bank
    suspend fun saveAllBanks(banks: List<Bank>): List<Bank>
}
