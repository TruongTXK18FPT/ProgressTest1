package com.truongtx.progresstest1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.truongtx.progresstest1.databinding.ActivityEditPaintingBinding
import com.bumptech.glide.Glide

class EditPaintingActivity : ComponentActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"         // "create" | "update"
        const val EXTRA_ITEM = "extra_item"         // Painting (parcelable)
        const val RESULT_KEY = "result_painting"
        const val RESULT_MODE = "result_mode"
    }

    private lateinit var b: ActivityEditPaintingBinding
    private var mode: String = "create"
    private var editing: Painting? = null
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            loadImagePreview()
        }
    }
    
    private fun loadImagePreview() {
        selectedImageUri?.let { uri ->
            b.progressImage.visibility = android.view.View.VISIBLE
            b.ivImagePlaceholder.visibility = android.view.View.GONE
            
            Glide.with(this)
                .load(uri)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .override(200, 200)
                .centerCrop()
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .into(b.ivPreview)
            
            // Hide progress after a short delay
            b.ivPreview.postDelayed({
                b.progressImage.visibility = android.view.View.GONE
                b.ivImagePlaceholder.visibility = android.view.View.GONE
            }, 1000)
            
            b.tvImageStatus.text = "Đã chọn ảnh"
        } ?: run {
            b.progressImage.visibility = android.view.View.GONE
            b.ivImagePlaceholder.visibility = android.view.View.VISIBLE
            b.ivPreview.setImageResource(android.R.drawable.ic_menu_gallery)
            b.tvImageStatus.text = "Chưa chọn ảnh"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditPaintingBinding.inflate(layoutInflater)
        setContentView(b.root)

        mode = intent.getStringExtra(EXTRA_MODE) ?: "create"
        editing = intent.getParcelableExtra(EXTRA_ITEM) as? Painting

        if (mode == "update" && editing != null) {
            b.edtTitle.setText(editing!!.title)
            b.edtAuthor.setText(editing!!.author)
            b.edtPrice.setText(editing!!.price.toString())
            b.edtDescription.setText(editing!!.description)
            b.cbIsNew.isChecked = editing!!.isNew
            b.cbIsOnSale.isChecked = editing!!.isOnSale
            
            // Load existing image if available
            if (!editing!!.imageUri.isNullOrEmpty()) {
                selectedImageUri = Uri.parse(editing!!.imageUri)
                loadImagePreview()
            }
        }

        b.btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        b.btnSave.setOnClickListener {
            val title = b.edtTitle.text.toString().trim()
            val author = b.edtAuthor.text.toString().trim()
            val price = b.edtPrice.text.toString().toDoubleOrNull() ?: 0.0
            val description = b.edtDescription.text.toString().trim()
            val isNew = b.cbIsNew.isChecked
            val isOnSale = b.cbIsOnSale.isChecked

            val result = if (mode == "update" && editing != null) {
                editing!!.copy(
                    title = title, 
                    author = author, 
                    price = price,
                    description = description,
                    isNew = isNew,
                    isOnSale = isOnSale,
                    imageUri = selectedImageUri?.toString() ?: editing!!.imageUri
                )
            } else {
                Painting(
                    title = title, 
                    author = author, 
                    price = price,
                    description = description,
                    isNew = isNew,
                    isOnSale = isOnSale,
                    imageUri = selectedImageUri?.toString()
                )
            }

            val data = Intent().apply {
                putExtra(RESULT_KEY, result)
                putExtra(RESULT_MODE, mode)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        b.btnCancel.setOnClickListener {
            finish()
        }
    }
}