package com.mobsol.pixabay.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalGridItemDecoration(
    private val spanCount: Int,
    private val topMargin: Int,
    private val rightMargin: Int,
    private val bottomMargin: Int,
    private val leftMargin: Int
) : RecyclerView.ItemDecoration() {
    constructor(spanCount: Int, margin: Int) : this(spanCount, margin, margin, margin, margin)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if(parent.getChildAdapterPosition(view) % spanCount == 0) {
            outRect.left = leftMargin
        } else if(parent.getChildAdapterPosition(view) % spanCount == 1){
            outRect.right = rightMargin
        }
    }
}