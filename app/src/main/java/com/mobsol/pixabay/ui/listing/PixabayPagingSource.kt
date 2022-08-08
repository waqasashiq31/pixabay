package com.mobsol.pixabay.ui.listing

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobsol.pixabay.BuildConfig
import com.mobsol.pixabay.model.ImageItem
import com.mobsol.pixabay.network.PixabayApiService
import okio.IOException
import retrofit2.HttpException

class PixabayPagingSource(private val apiService: PixabayApiService, private val query: String) :
    PagingSource<Int, ImageItem>() {

    /**
     * Provide a [Key] used for the initial [load] for the next [PagingSource] due to invalidation
     * of this [PagingSource]. The [Key] is provided to [load] via [LoadParams.key].
     *
     * The [Key] returned by this method should cause [load] to load enough items to
     * fill the viewport around the last accessed position, allowing the next generation to
     * transparently animate in. The last accessed position can be retrieved via
     * [state.anchorPosition][PagingState.anchorPosition], which is typically
     * the top-most or bottom-most item in the viewport due to access being triggered by binding
     * items as they scroll into view.
     *
     * For example, if items are loaded based on integer position keys, you can return
     * [state.anchorPosition][PagingState.anchorPosition].
     *
     * Alternately, if items contain a key used to load, get the key from the item in the page at
     * index [state.anchorPosition][PagingState.anchorPosition].
     *
     * @param state [PagingState] of the currently fetched data, which includes the most recently
     * accessed position in the list via [PagingState.anchorPosition].
     *
     * @return [Key] passed to [load] after invalidation used for initial load of the next
     * generation. The [Key] returned by [getRefreshKey] should load pages centered around
     * user's current viewport. If the correct [Key] cannot be determined, `null` can be returned
     * to allow [load] decide what default key to use.
     */
    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /**
     * Loading API for [PagingSource].
     *
     * Implement this method to trigger your async load (e.g. from database or network).
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: FIRST_PAGE
            val response = apiService.searchImageByText(
                BuildConfig.PIXABAY_API_KEY,
                query,
                QUERY_TYPE,
                nextPageNumber,
                params.loadSize
            )
            val imageItems = response.hits
            val nextKey = if (imageItems.isEmpty() || nextPageNumber >= (response.totalHits / NETWORK_PAGE_SIZE) + 1) {
                null
            } else {
                nextPageNumber + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = imageItems,
                prevKey = null, // Only paging forward.
                nextKey = nextKey
            )

        } catch (e: IOException) {
            LoadResult.Error(Throwable("Internet connection error"))
        } catch (e: HttpException) {
            LoadResult.Error(Throwable("Server connection error"))
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE: Int = 1
        const val QUERY_TYPE = "photo"
        const val NETWORK_PAGE_SIZE = 40
    }
}