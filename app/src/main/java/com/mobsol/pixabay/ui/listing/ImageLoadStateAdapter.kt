package com.mobsol.pixabay.ui.listing

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class ImageLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ImageLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ImageLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ImageLoadStateViewHolder {
        return ImageLoadStateViewHolder.create(parent, retry)
    }
}