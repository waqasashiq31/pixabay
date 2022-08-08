package com.mobsol.pixabay.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageItem(
    val id: Long,
    val pageURL: String,
    val type: String,
    val tags: String,
    val previewURL: String,
    val previewWidth: Int,
    val previewHeight: Int,
    val webformatURL: String,
    val webformatWidth: Int,
    val webformatHeight: Int,
    val largeImageURL: String,
    val fullHDURL: String?,
    val imageURL: String?,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageSize: Long,
    val views: Long,
    val downloads: Long,
    val likes: Int,
    val comments: Int,
    @SerializedName("user_id") val userId: Long,
    val user: String,
    val userImageURL: String,
) : Parcelable
