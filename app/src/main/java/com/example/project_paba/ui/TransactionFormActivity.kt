package com.example.financeapp.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.R
import com.example.financeapp.model.Category
import com.example.financeapp.model.Transaction
import com.example.financeapp.model.TransactionType
import com.example.financeapp.storage.PreferenceHelper

class TransactionFormActivity : AppCompatActivity() {

    private lateinit var prefHelper: PreferenceHelper
    private lateinit var tvFormTitle: TextView
    private lateinit var spCategory: Spinner
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    private var transactionType: TransactionType = TransactionType.EXPENSE
    private var editingTransactionId: String? = null
    private var filteredCategories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_form)

        prefHelper = PreferenceHelper(this)
        tvFormTitle = findViewById(R.id.tvFormTitle)
        spCategory = findViewById(R.id.spCategory)
        etAmount = findViewById(R.id.etAmount)
        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSaveTransaction)
        btnDelete = findViewById(R.id.btnDeleteTransaction)

        val typeStr = intent.getStringExtra("EXTRA_TYPE") ?: TransactionType.EXPENSE.name
        transactionType = TransactionType.valueOf(typeStr)

        editingTransactionId = intent.getStringExtra("EXTRA_TRANSACTION_ID")

        setupCategorySpinner()
        setupMode()

        btnSave.setOnClickListener { saveOrUpdateTransaction() }
        btnDelete.setOnClickListener { confirmDelete() }
    }

    private fun setupCategorySpinner() {
        filteredCategories = prefHelper.getCategories().filter { it.type == transactionType }
        val categoryNames = filteredCategories.map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter
    }

    private fun setupMode() {
        val label = if (transactionType == TransactionType.INCOME) "Pemasukan (Income)" else "Pengeluaran (Expense)"

        if (editingTransactionId != null) {
            tvFormTitle.text = "Ubah $label"
            btnSave.text = "Update Data"
            btnDelete.visibility = View.VISIBLE

            val existing = prefHelper.getAllTransactions().find { it.id == editingTransactionId }
            existing?.let { trx ->
                etAmount.setText(trx.amount.toBigDecimal().toPlainString())
                etNote.setText(trx.note)
                val catIndex = filteredCategories.indexOfFirst { it.id == trx.categoryId }
                if (catIndex != -1) spCategory.setSelection(catIndex)
            }
        } else {
            tvFormTitle.text = "Catat $label"
        }
    }

    private fun saveOrUpdateTransaction() {
        if (filteredCategories.isEmpty()) {
            Toast.makeText(this, "Buat kategori terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        val amountStr = etAmount.text.toString().trim()
        val note = etNote.text.toString().trim()

        if (amountStr.isEmpty() || amountStr.toDoubleOrNull() == null || amountStr.toDouble() <= 0) {
            etAmount.error = "Masukkan nominal yang valid"
            return
        }

        val selectedCategory = filteredCategories[spCategory.selectedItemPosition]
        val amount = amountStr.toDouble()

        if (editingTransactionId == null) {
            val newTransaction = Transaction(
                amount = amount,
                note = note.ifEmpty { selectedCategory.name },
                categoryId = selectedCategory.id,
                categoryName = selectedCategory.name,
                type = transactionType
            )
            prefHelper.addTransaction(newTransaction)
            Toast.makeText(this, "Data berhasil dicatat", Toast.LENGTH_SHORT).show()
        } else {
            val oldTransaction = prefHelper.getAllTransactions().find { it.id == editingTransactionId }
            val updated = Transaction(
                id = editingTransactionId!!,
                amount = amount,
                note = note.ifEmpty { selectedCategory.name },
                categoryId = selectedCategory.id,
                categoryName = selectedCategory.name,
                type = transactionType,
                date = oldTransaction?.date ?: System.currentTimeMillis()
            )
            prefHelper.updateTransaction(updated)
            Toast.makeText(this, "Data berhasil diubah", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Yakin ingin menghapus transaksi ini?")
            .setPositiveButton("Hapus") { _, _ ->
                editingTransactionId?.let { prefHelper.deleteTransaction(it) }
                Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
