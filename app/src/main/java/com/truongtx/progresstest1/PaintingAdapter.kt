package com.truongtx.progresstest1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.truongtx.progresstest1.databinding.ItemPaintingBinding
import com.bumptech.glide.Glide
import android.net.Uri

class PaintingAdapter(
    private val onEdit: (Painting) -> Unit,
    private val onDelete: (Painting) -> Unit
) : ListAdapter<Painting, PaintingAdapter.VH>(PaintingDiff()) {

    inner class VH(val b: ItemPaintingBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPaintingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.b) {
            tvTitle.text = item.title
            tvAuthor.text = item.author
            tvPrice.text = String.format("%.0f VND", item.price)
            tvDescription.text = item.description
            tvDescription.visibility = if (item.description.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            
            // Show/hide badges
            chipNew.visibility = if (item.isNew) android.view.View.VISIBLE else android.view.View.GONE
            chipSale.visibility = if (item.isOnSale) android.view.View.VISIBLE else android.view.View.GONE
            
            // Load image with optimization
            if (!item.imageUri.isNullOrEmpty()) {
                progressBar.visibility = android.view.View.VISIBLE
                ivPlaceholder.visibility = android.view.View.GONE
                
                Glide.with(ivPainting.context)
                    .load(Uri.parse(item.imageUri))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .override(200, 200) // Resize for better performance
                    .centerCrop()
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .into(ivPainting)
                
                // Hide progress after a short delay
                ivPainting.postDelayed({
                    progressBar.visibility = android.view.View.GONE
                    ivPlaceholder.visibility = android.view.View.GONE
                }, 1000)
            } else {
                progressBar.visibility = android.view.View.GONE
                ivPlaceholder.visibility = android.view.View.VISIBLE
                ivPainting.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}