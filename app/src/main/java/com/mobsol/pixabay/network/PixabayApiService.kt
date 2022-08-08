package com.mobsol.pixabay.network

import com.mobsol.pixabay.model.ImageSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {

    @GET("api")
    suspend fun searchImageByText(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("image_type") imageType: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): ImageSearchResponse
}