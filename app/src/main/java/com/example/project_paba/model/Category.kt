package com.example.financeapp.model

import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: TransactionType
)
