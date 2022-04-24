package de.hennihaus.services

import de.hennihaus.models.Bank

interface BankService {
    suspend fun getAllBanks(): List<Bank>
    suspend fun getBankByJmsTopic(jmsTopic: String): Bank
    suspend fun updateBank(bank: Bank): Bank
    suspend fun updateAllBanks(banks: List<Bank>): List<Bank>
}
