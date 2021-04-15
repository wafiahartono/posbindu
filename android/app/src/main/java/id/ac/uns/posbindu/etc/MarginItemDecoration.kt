package id.ac.uns.posbindu.etc

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        outRect.left = 0
        outRect.top = 0
        outRect.right = 0
        outRect.bottom = 0
        parent.layoutManager?.let { layoutManager ->
            val isHorizontal: Boolean
            val column: Int
            when (layoutManager) {
                is GridLayoutManager -> {
                    isHorizontal = false
                    column = layoutManager.spanCount
                }
                is LinearLayoutManager -> {
                    isHorizontal = layoutManager.orientation == LinearLayoutManager.HORIZONTAL
                    column = 1
                }
                else -> return@let
            }
            if (isHorizontal) {
                outRect.left = if (position > 0) spacing else 0
            } else {
                outRect.top = if (position > column - 1) spacing else 0
                outRect.right = if (position % column != column - 1) spacing else 0
            }
        }
    }
}