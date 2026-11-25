package com.example.financeapp.model

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val note: String,
    val categoryId: String,
    val categoryName: String,
    val type: TransactionType,
    val date: Long = System.currentTimeMillis() // Timestamp untuk sorting & filter bulan
)
