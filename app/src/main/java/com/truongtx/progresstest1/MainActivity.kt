package com.truongtx.progresstest1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongtx.progresstest1.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : ComponentActivity() {

    private lateinit var b: ActivityMainBinding
    private val adapter by lazy {
        PaintingAdapter(
            onEdit = { editPainting(it) },
            onDelete = { deletePainting(it) }
        )
    }
    private val items = mutableListOf(
        Painting(title="Trúc Chỉ Sen Vàng", author="Mộc Linh", price=3_500_000.0, description="Tranh trúc chỉ sen vàng đẹp tuyệt vời", isNew=true),
        Painting(title="Phong Cảnh Hội An", author="An Nhiên", price=2_200_000.0, description="Phong cảnh Hội An cổ kính", isOnSale=true)
    )
    private var filteredItems = items.toList()

    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK && res.data != null) {
            val returned = res.data!!.getParcelableExtra(EditPaintingActivity.RESULT_KEY) as? Painting
            val mode = res.data!!.getStringExtra(EditPaintingActivity.RESULT_MODE)
            if (returned != null && mode != null) {
                when (mode) {
                    "create" -> {
                        items.add(0, returned)
                        submit()
                        Snackbar.make(b.root, "Đã thêm tranh mới", Snackbar.LENGTH_SHORT).show()
                    }
                    "update" -> {
                        val idx = items.indexOfFirst { it.id == returned.id }
                        if (idx >= 0) {
                            items[idx] = returned
                            submit()
                            Snackbar.make(b.root, "Đã cập nhật", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.rvPainting.layoutManager = LinearLayoutManager(this)
        b.rvPainting.adapter = adapter
        submit()

        b.fabAdd.setOnClickListener { addPainting() }
        
        // Setup search functionality
        setupSearch()
        
        // Setup filter chips
        setupFilterChips()
        
        // Setup toolbar
        setupToolbar()
    }

    private fun submit() = adapter.submitList(filteredItems)
    
    private fun setupSearch() {
        b.edtSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                filterItems(s?.toString() ?: "")
            }
        })
    }
    
    private fun setupFilterChips() {
        b.chipAll.setOnClickListener { filterByType("all") }
        b.chipSale.setOnClickListener { filterByType("sale") }
        b.chipNew.setOnClickListener { filterByType("new") }
        b.chipSortPrice.setOnClickListener { sortByPrice() }
    }
    
    private fun setupToolbar() {
        b.topAppBar.setNavigationOnClickListener {
            // Handle back navigation if needed
        }
    }
    
    private fun filterItems(query: String) {
        filteredItems = items.filter { painting ->
            painting.title.contains(query, ignoreCase = true) ||
            painting.author.contains(query, ignoreCase = true) ||
            painting.description.contains(query, ignoreCase = true)
        }
        submit()
    }
    
    private fun filterByType(type: String) {
        filteredItems = when (type) {
            "sale" -> items.filter { it.isOnSale }
            "new" -> items.filter { it.isNew }
            else -> items
        }
        submit()
    }
    
    private fun sortByPrice() {
        filteredItems = filteredItems.sortedByDescending { it.price }
        submit()
    }

    private fun addPainting() {
        val intent = Intent(this, EditPaintingActivity::class.java).apply {
            putExtra(EditPaintingActivity.EXTRA_MODE, "create")
        }
        editLauncher.launch(intent)
    }

    private fun editPainting(p: Painting) {
        val intent = Intent(this, EditPaintingActivity::class.java).apply {
            putExtra(EditPaintingActivity.EXTRA_MODE, "update")
            putExtra(EditPaintingActivity.EXTRA_ITEM, p)
        }
        editLauncher.launch(intent)
    }

    private fun deletePainting(p: Painting) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa tranh \"${p.title}\"?")
            .setPositiveButton("Xóa") { _, _ ->
                val idx = items.indexOfFirst { it.id == p.id }
                if (idx >= 0) {
                    items.removeAt(idx)
                    filterItems(b.edtSearch.text.toString())
                    Snackbar.make(b.root, "Đã xóa \"${p.title}\"", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}