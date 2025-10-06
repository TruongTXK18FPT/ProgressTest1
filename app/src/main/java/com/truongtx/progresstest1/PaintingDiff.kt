package com.truongtx.progresstest1

import androidx.recyclerview.widget.DiffUtil

class PaintingDiff : DiffUtil.ItemCallback<Painting>() {
    override fun areItemsTheSame(oldItem: Painting, newItem: Painting) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Painting, newItem: Painting) = oldItem == newItem
}