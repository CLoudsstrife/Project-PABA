package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.project.model.Transaction
import com.example.project.model.TransactionType
import com.example.project.storage.PreferenceHelper
import com.example.project.ui.CategoryActivity
import com.example.project.ui.TransactionFormActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefHelper: PreferenceHelper
    private lateinit var lvRecent: ListView
    private var transactionList = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefHelper = PreferenceHelper(this)
        lvRecent = findViewById(R.id.lvRecentTransactions)

        findViewById<Button>(R.id.btnManageCategory).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddIncome).setOnClickListener {
            val intent = Intent(this, TransactionFormActivity::class.java)
            intent.putExtra("EXTRA_TYPE", TransactionType.INCOME.name)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            val intent = Intent(this, TransactionFormActivity::class.java)
            intent.putExtra("EXTRA_TYPE", TransactionType.EXPENSE.name)
            startActivity(intent)
        }

        lvRecent.setOnItemClickListener { _, _, position, _ ->
            val selected = transactionList[position]
            val intent = Intent(this, TransactionFormActivity::class.java).apply {
                putExtra("EXTRA_TYPE", selected.type.name)
                putExtra("EXTRA_TRANSACTION_ID", selected.id)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        transactionList = prefHelper.getAllTransactions()
        val displayList = transactionList.map { trx ->
            val sign = if (trx.type == TransactionType.INCOME) "+" else "-"
            "$sign Rp${trx.amount.toInt()} | ${trx.categoryName} (${trx.note})"
        }
        lvRecent.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
    }
}
