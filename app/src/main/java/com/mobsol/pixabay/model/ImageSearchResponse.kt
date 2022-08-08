package com.mobsol.pixabay.model

data class ImageSearchResponse(val total: Long, val totalHits: Long, val hits: List<ImageItem>)