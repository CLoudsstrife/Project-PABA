package com.example.financeapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.R
import com.example.financeapp.model.Category
import com.example.financeapp.model.TransactionType
import com.example.financeapp.storage.PreferenceHelper

class CategoryActivity : AppCompatActivity() {

    private lateinit var prefHelper: PreferenceHelper
    private lateinit var etCategoryName: EditText
    private lateinit var rgCategoryType: RadioGroup
    private lateinit var btnSaveCategory: Button
    private lateinit var lvCategories: ListView
    private var categoryList = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        prefHelper = PreferenceHelper(this)
        etCategoryName = findViewById(R.id.etCategoryName)
        rgCategoryType = findViewById(R.id.rgCategoryType)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)
        lvCategories = findViewById(R.id.lvCategories)

        loadCategories()

        btnSaveCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isEmpty()) {
                etCategoryName.error = "Nama kategori wajib diisi"
                return@setOnClickListener
            }

            val type = if (rgCategoryType.checkedRadioButtonId == R.id.rbIncome) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }

            prefHelper.addCategory(Category(name = name, type = type))
            etCategoryName.text.clear()
            Toast.makeText(this, "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
            loadCategories()
        }

        lvCategories.setOnItemClickListener { _, _, position, _ ->
            val selected = categoryList[position]
            AlertDialog.Builder(this)
                .setTitle("Hapus Kategori")
                .setMessage("Yakin ingin menghapus kategori '${selected.name}'?")
                .setPositiveButton("Hapus") { _, _ ->
                    prefHelper.deleteCategory(selected.id)
                    loadCategories()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun loadCategories() {
        categoryList = prefHelper.getCategories()
        val displayList = categoryList.map { "${it.name} [${it.type.name}]" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        lvCategories.adapter = adapter
    }
}
