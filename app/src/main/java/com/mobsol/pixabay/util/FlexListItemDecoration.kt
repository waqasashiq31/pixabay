package com.mobsol.pixabay.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FlexListItemDecoration(
    private val topMargin: Int,
    private val rightMargin: Int,
    private val bottomMargin: Int,
    private val leftMargin: Int
) : RecyclerView.ItemDecoration() {
    constructor(margin: Int) : this(margin, margin, margin, margin)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = leftMargin
        outRect.right = rightMargin
        outRect.top = topMargin
        outRect.bottom = bottomMargin
    }
}