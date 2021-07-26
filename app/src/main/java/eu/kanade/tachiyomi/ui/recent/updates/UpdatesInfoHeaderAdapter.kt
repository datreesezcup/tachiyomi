package eu.kanade.tachiyomi.ui.recent.updates

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.databinding.UpdatesLastinfoHeaderBinding
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.Date
import eu.kanade.tachiyomi.data.library.LibraryUpdateService.Trigger as UpdateTrigger

class UpdatesInfoHeaderAdapter() :
    RecyclerView.Adapter<UpdatesInfoHeaderAdapter.Holder>() {

    private val preferences: PreferencesHelper = Injekt.get()

    private lateinit var binding: UpdatesLastinfoHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        binding = UpdatesLastinfoHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding.root)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = 1

    inner class Holder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            /* Making "now" as 30 sec in future, so that it is always after lastUpdateTimestamp
               because otherwise when updating from the UpdateController for example, the header may say
               "In 0 minutes"
             */
            val now = Date().time + 30000L

            val context = view.context

            val lastUpdateTimestamp = preferences.libraryUpdateLastTimestamp().get()
            val lastUpdateTimeString = DateUtils.getRelativeTimeSpanString(lastUpdateTimestamp, now, DateUtils.MINUTE_IN_MILLIS)

            val updateReason = when (preferences.libraryUpdateLastTrigger().get()) {
                UpdateTrigger.AUTOMATIC -> R.string.update_trigger_automatic
                UpdateTrigger.MANUAL -> R.string.update_trigger_manual
            }.let(context::getString)

            binding.title.text = context.getString(R.string.updates_last_update_info, lastUpdateTimeString, updateReason)
        }
    }
}
