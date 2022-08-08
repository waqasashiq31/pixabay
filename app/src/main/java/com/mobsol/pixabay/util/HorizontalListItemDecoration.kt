package com.mobsol.pixabay.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalListItemDecoration(
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
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = leftMargin + rightMargin
            outRect.bottom = bottomMargin + topMargin
        } else if (parent.layoutManager != null && parent.getChildAdapterPosition(view) == parent.layoutManager!!
                .itemCount - 1
        ) {
            outRect.right = leftMargin + rightMargin
            outRect.left = leftMargin
            outRect.bottom = bottomMargin + topMargin
        } else {
            outRect.left = leftMargin
            outRect.bottom = bottomMargin + topMargin
        }
    }
}