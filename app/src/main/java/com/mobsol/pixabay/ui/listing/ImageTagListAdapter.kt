package com.mobsol.pixabay.ui.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobsol.pixabay.R
import com.mobsol.pixabay.databinding.ImageTagItemBinding

class ImageTagListAdapter (private val tagList: List<String>): RecyclerView.Adapter<ImageTagListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.image_tag_item,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tagList[position]
        holder.bindUI(item)

    }

    override fun getItemCount(): Int = tagList.size

    inner class ViewHolder(private val binding: ImageTagItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindUI(item: String) {
            binding.tag = item
        }
    }

}