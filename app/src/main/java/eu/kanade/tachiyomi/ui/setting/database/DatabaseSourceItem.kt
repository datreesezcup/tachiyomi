package eu.kanade.tachiyomi.ui.setting.database

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.databinding.DatabaseSourceItemBinding
import eu.kanade.tachiyomi.source.LocalSource
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.icon
import eu.kanade.tachiyomi.util.system.LocaleHelper

class DatabaseSourceItem(val source: Source, val mangaCount: Int) :
    AbstractFlexibleItem<DatabaseSourceItem.Holder>() {

    override fun getLayoutRes(): Int {
        return R.layout.database_source_item
    }

    @Suppress("unchecked_cast")
    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): Holder {
        return Holder(
            view!!,
            adapter as FlexibleAdapter<DatabaseSourceItem>
        )
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: Holder, position: Int, payloads: MutableList<Any>?) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DatabaseSourceItem) source.id == other.source.id else false
    }

    override fun hashCode() = source.id.hashCode()

    inner class Holder(view: View, val adapter: FlexibleAdapter<DatabaseSourceItem>) :
        FlexibleViewHolder(view, adapter) {

        private val binding = DatabaseSourceItemBinding.bind(view)

        fun bind(item: DatabaseSourceItem) {
            binding.title.text = if (item.source.id != LocalSource.ID) {
                "${item.source.name} (${LocaleHelper.getSourceDisplayName(item.source.lang, itemView.context)})"
            } else item.source.name

            binding.description.text = itemView.context.getString(R.string.database_source_manga_count, item.mangaCount)
            binding.checkbox.isChecked = adapter.isSelected(bindingAdapterPosition)
            itemView.post {
                when {
                    item.source.id == LocalSource.ID -> binding.thumbnail.setImageResource(R.mipmap.ic_local_source)
                    item.source.icon() != null -> binding.thumbnail.setImageDrawable(item.source.icon())
                }
            }
        }
    }
}
