package com.mobsol.pixabay.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mobsol.pixabay.model.ImageItem
import com.mobsol.pixabay.network.PixabayApiService
import com.mobsol.pixabay.ui.listing.PixabayPagingSource
import com.mobsol.pixabay.ui.listing.PixabayPagingSource.Companion.NETWORK_PAGE_SIZE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PixabayRepository @Inject constructor(@ApplicationContext context: Context, private val apiService: PixabayApiService) {

    fun getImageSearchResultStream(query: String): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PixabayPagingSource(apiService, query) }
        ).flow
    }
}