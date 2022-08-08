package com.mobsol.pixabay.ui.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mobsol.pixabay.R
import com.mobsol.pixabay.databinding.ImageLoadStateFooterViewItemBinding

class ImageLoadStateViewHolder(
    private val binding: ImageLoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRetry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.tvErrorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.btnRetry.isVisible = loadState is LoadState.Error
        binding.tvErrorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ImageLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_load_state_footer_view_item, parent, false)
            val binding = ImageLoadStateFooterViewItemBinding.bind(view)
            return ImageLoadStateViewHolder(binding, retry)
        }
    }
}