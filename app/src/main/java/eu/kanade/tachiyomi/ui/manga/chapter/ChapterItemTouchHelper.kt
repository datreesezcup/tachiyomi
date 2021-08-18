package eu.kanade.tachiyomi.ui.manga.chapter

import android.graphics.Canvas
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import eu.kanade.tachiyomi.ui.manga.chapter.base.BaseChapterHolder
import eu.kanade.tachiyomi.ui.manga.chapter.base.BaseChaptersAdapter

class ChapterItemTouchHelper : ItemTouchHelper.Callback() {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val chapterAdapter = viewHolder.bindingAdapter as? BaseChaptersAdapter<*> ?: return
        chapterAdapter.onItemSwiped(viewHolder.bindingAdapterPosition, direction)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val adapter = viewHolder.bindingAdapter as? BaseChaptersAdapter<*>
        return if (adapter != null && adapter.selectedItemCount == 0) {
            makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        } else 0
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val chapterHolder = viewHolder as? BaseChapterHolder ?: return
        chapterHolder.rearRightView.isVisible = false
        chapterHolder.rearLeftView.isVisible = false
        chapterHolder.frontView.translationX = 0f
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val chapterHolder = viewHolder as? BaseChapterHolder ?: return
        getDefaultUIUtil().onDraw(c, recyclerView, chapterHolder.frontView, dX, dY, actionState, isCurrentlyActive)
        viewHolder.rearLeftView.isVisible = dX > 0
        viewHolder.rearRightView.isVisible = dX < 0
    }
}
