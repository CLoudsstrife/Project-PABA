package com.example.financeapp.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.financeapp.model.Category
import com.example.financeapp.model.Transaction
import com.example.financeapp.model.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("finance_app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_CATEGORIES = "key_categories"
        private const val KEY_TRANSACTIONS = "key_transactions"
    }

    fun getCategories(): MutableList<Category> {
        val json = prefs.getString(KEY_CATEGORIES, null) ?: return getDefaultCategories()
        val type = object : TypeToken<MutableList<Category>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveCategories(list: List<Category>) {
        prefs.edit().putString(KEY_CATEGORIES, gson.toJson(list)).apply()
    }

    fun addCategory(category: Category) {
        val list = getCategories()
        list.add(category)
        saveCategories(list)
    }

    fun deleteCategory(categoryId: String) {
        val list = getCategories()
        list.removeAll { it.id == categoryId }
        saveCategories(list)
    }

    private fun getDefaultCategories(): MutableList<Category> {
        val defaults = mutableListOf(
            Category(name = "Gaji", type = TransactionType.INCOME),
            Category(name = "Bonus", type = TransactionType.INCOME),
            Category(name = "Makan & Minum", type = TransactionType.EXPENSE),
            Category(name = "Transportasi", type = TransactionType.EXPENSE)
        )
        saveCategories(defaults)
        return defaults
    }

    fun getAllTransactions(): MutableList<Transaction> {
        val json = prefs.getString(KEY_TRANSACTIONS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveTransactions(list: List<Transaction>) {
        prefs.edit().putString(KEY_TRANSACTIONS, gson.toJson(list)).apply()
    }

    fun addTransaction(transaction: Transaction) {
        val list = getAllTransactions()
        list.add(transaction)
        saveTransactions(list)
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val list = getAllTransactions()
        val index = list.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            list[index] = updatedTransaction
            saveTransactions(list)
        }
    }

    fun deleteTransaction(transactionId: String) {
        val list = getAllTransactions()
        list.removeAll { it.id == transactionId }
        saveTransactions(list)
    }

    fun getIncomes(): List<Transaction> {
        return getAllTransactions().filter { it.type == TransactionType.INCOME }
    }

    fun getExpenses(): List<Transaction> {
        return getAllTransactions().filter { it.type == TransactionType.EXPENSE }
    }
}
