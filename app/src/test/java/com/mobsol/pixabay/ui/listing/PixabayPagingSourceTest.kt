package com.mobsol.pixabay.ui.listing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.google.common.truth.Truth
import com.mobsol.pixabay.BuildConfig
import com.mobsol.pixabay.MainCoroutineRule
import com.mobsol.pixabay.model.ImageItem
import com.mobsol.pixabay.model.ImageSearchResponse
import com.mobsol.pixabay.network.PixabayApiService
import com.mobsol.pixabay.ui.listing.PixabayPagingSource.Companion.NETWORK_PAGE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.exceptions.base.MockitoException
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PixabayPagingSourceTest {

    @Mock
    lateinit var apiService: PixabayApiService

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineTestRule = MainCoroutineRule()

    lateinit var pixabayPagingSource: PixabayPagingSource

    companion object {
        val mockResponse = ImageSearchResponse(
            total = 3000,
            totalHits = 500,
            hits = listOf(
                ImageItem(
                    634572, "https://pixabay.com/photos/apples-fruits-red-ripe-vitamins-634572/",
                    "photo",
                    "apples, fruits, red",
                    "https://cdn.pixabay.com/photo/2015/02/13/00/43/apples-634572_150.jpg",
                    100,
                    150,
                    "https://pixabay.com/get/g5790a60e103acfd8598471d8e352a68ad316e036fdb149f8fd0a86a8e6a0ba679e00aff31868777feb13d54fb784d919_640.jpg",
                    427,
                    640,
                    "https://pixabay.com/get/gc64ee6ca358b09d2b01e4db571b3870bbcde9f5c8568217cb7a9281485a850758158866c289b8558e9617cec375ea7c84b9b5aba58c817be762c5c84ac27f6dc_1280.jpg",
                    null,
                    null,
                    5017,
                    811238,
                    409702,
                    235033,
                    2948,
                    2295,
                    180,
                    752536,
                    "Desertrose7",
                    "https://cdn.pixabay.com/user/2016/03/14/13-25-18-933_250x250.jpg"
                )
            )
        )
        val mockResponse2 = ImageSearchResponse(
            total = 1000,
            totalHits = 0,
            hits = listOf()
        )
    }

    @Before
    fun setup() {
        pixabayPagingSource = PixabayPagingSource(apiService, "fruits")
    }

    @Test
    fun `Verify next page key when data loads successfully`() = runTest {
        Mockito.`when`(apiService.searchImageByText(BuildConfig.PIXABAY_API_KEY, "fruits", "photo", 1, NETWORK_PAGE_SIZE)).thenReturn(
            mockResponse)
        val expectedNextPageKey = 2
        val expectedResult = PagingSource.LoadResult.Page(mockResponse.hits, null, expectedNextPageKey)
        Truth.assertThat(
            pixabayPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    loadSize = NETWORK_PAGE_SIZE,
                    placeholdersEnabled = false,
                    key = null
                )
            )
        ).isEqualTo(expectedResult)
    }

    @Test
    fun `Invalidate next page key when all pages are loaded`() = runTest {
        Mockito.`when`(apiService.searchImageByText(BuildConfig.PIXABAY_API_KEY, "fruits", "photo", 1, NETWORK_PAGE_SIZE)).thenReturn(
            mockResponse2)
        val expectedNextPageKey = null
        val expectedResult = PagingSource.LoadResult.Page(listOf(), null, expectedNextPageKey)
        Truth.assertThat(
            pixabayPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    loadSize = NETWORK_PAGE_SIZE,
                    placeholdersEnabled = false,
                    key = null
                )
            )
        ).isEqualTo(expectedResult)
    }

    @Test
    fun `Throw exception when there's an error while loading any page `() = runTest {
        Mockito.`when`(
            apiService.searchImageByText(
                BuildConfig.PIXABAY_API_KEY,
                "fruits",
                "photo",
                1,
                NETWORK_PAGE_SIZE
            )
        ).thenThrow(MockitoException("Error", IOException()))

        val expectedResult = PagingSource.LoadResult.Error<Int, ImageItem>(IOException())
        val actualResult = pixabayPagingSource.load(
            PagingSource.LoadParams.Refresh(
                loadSize = NETWORK_PAGE_SIZE,
                placeholdersEnabled = false,
                key = null
            )
        )
        Truth.assertThat(actualResult) === (expectedResult)
    }
}