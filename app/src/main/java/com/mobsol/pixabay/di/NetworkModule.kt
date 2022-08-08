package com.mobsol.pixabay.di

import android.content.Context
import com.mobsol.pixabay.BuildConfig
import com.mobsol.pixabay.network.PixabayApiService
import com.mobsol.pixabay.util.RetrofitCacheInterceptor
import com.mobsol.pixabay.util.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object NetworkModule {

    @Provides
    fun providePixabayApiService(@ApplicationContext context: Context): PixabayApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.PIXABAY_BASE_URL)
            .client(getHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PixabayApiService::class.java)
    }

    private fun getHttpClient(context: Context): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val mCache = Cache(context.cacheDir, cacheSize)

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder().apply {
            cache(mCache)
            connectTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            addInterceptor(RetrofitCacheInterceptor(context))
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            if (BuildConfig.DEBUG) {
                addInterceptor(logging)
            }
        }
        return httpClient.build()
    }
}