package com.mobsol.pixabay.ui.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobsol.pixabay.R
import com.mobsol.pixabay.databinding.ImageListItemBinding
import com.mobsol.pixabay.model.ImageItem

class ImageListAdapter(private val itemClickListener: (imageItem: ImageItem) -> Unit) :
    PagingDataAdapter<ImageItem, ImageListAdapter.ViewHolder>(ImageItemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.image_list_item,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bindUI(item) }

    }

    inner class ViewHolder(private val binding: ImageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindUI(item: ImageItem) {
            binding.imageItem = item
            binding.root.setOnClickListener {
                itemClickListener(item)
            }
        }
    }

}

object ImageItemComparator : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }
}