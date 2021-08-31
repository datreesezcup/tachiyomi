package eu.kanade.tachiyomi.ui.setting.database

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.databinding.DatabaseSourcesControllerBinding
import eu.kanade.tachiyomi.ui.base.controller.DialogController
import eu.kanade.tachiyomi.ui.base.controller.FabController
import eu.kanade.tachiyomi.ui.base.controller.NucleusController
import eu.kanade.tachiyomi.util.system.toast
import eu.kanade.tachiyomi.util.view.setTooltip
import eu.kanade.tachiyomi.util.view.shrinkOnScroll

class DatabaseSourcesController :
    NucleusController<DatabaseSourcesControllerBinding, DatabaseSourcesPresenter>(),
    FlexibleAdapter.OnItemClickListener,
    FlexibleAdapter.OnUpdateListener,
    FabController {

    private var recycler: RecyclerView? = null

    private var adapter: FlexibleAdapter<DatabaseSourceItem>? = null

    private var actionFab: ExtendedFloatingActionButton? = null

    private var actionFabScrollListener: RecyclerView.OnScrollListener? = null

    private var menu: Menu? = null

    init {
        setHasOptionsMenu(true)
    }

    private val selectedSources: List<DatabaseSourceItem>
        get() = adapter?.selectedPositions?.mapNotNull(adapter!!::getItem) ?: emptyList()

    override fun getTitle(): String {
        return activity!!.getString(R.string.pref_clear_database)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        adapter = FlexibleAdapter<DatabaseSourceItem>(null, this, true)
        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        binding.recycler.adapter = adapter
        actionFabScrollListener = binding.recycler.let { actionFab?.shrinkOnScroll(it) }
        recycler = binding.recycler

        adapter?.fastScroller = binding.fastScroller
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun createBinding(inflater: LayoutInflater): DatabaseSourcesControllerBinding {
        return DatabaseSourcesControllerBinding.inflate(inflater)
    }

    override fun createPresenter(): DatabaseSourcesPresenter {
        return DatabaseSourcesPresenter()
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        toggleSelection(position)
        adapter!!.notifyItemChanged(position)
        return true
    }

    override fun configureFab(fab: ExtendedFloatingActionButton) {
        fab.setIconResource(R.drawable.ic_delete_24dp)
        fab.setText(R.string.action_delete)
        fab.isVisible = false
        fab.setTooltip(R.string.action_delete)
        fab.setOnClickListener {
            val ctrl = ClearDatabaseSourcesDialog()
            ctrl.targetController = this
            ctrl.showDialog(router)
        }
        actionFab = fab
    }

    override fun cleanupFab(fab: ExtendedFloatingActionButton) {
        actionFab?.setOnClickListener(null)
        actionFabScrollListener?.let { recycler?.removeOnScrollListener(it) }
        actionFab = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.generic_selection, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val adapter = adapter ?: return super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.action_select_all -> {
                for (i in 0 until adapter.itemCount) {
                    addSelection(i)
                }
            }
            R.id.action_select_inverse -> {
                for (i in 0 until adapter.itemCount) {
                    toggleSelection(i)
                }
            }
        }

        adapter.notifyItemRangeChanged(0, adapter.itemCount)
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            binding.emptyView.hide()
        } else {
            binding.emptyView.show(R.string.database_clean_message)
        }

        menu?.apply {
            get(0).isVisible = size > 0
            get(1).isVisible = size > 0
        }
    }

    fun setDatabaseSources(sources: List<DatabaseSourceItem>) {
        adapter?.updateDataSet(sources)
    }

    private fun addSelection(position: Int) {
        val adapter = adapter ?: return
        adapter.addSelection(position)
        actionFab!!.isVisible = true
    }

    private fun toggleSelection(position: Int) {
        val adapter = adapter ?: return
        adapter.toggleSelection(position)

        actionFab!!.isVisible = adapter.selectedItemCount > 0
    }

    class ClearDatabaseSourcesDialog : DialogController() {

        override fun onCreateDialog(savedViewState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(activity!!)
                .setMessage(R.string.clear_database_confirmation)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    (targetController as? DatabaseSourcesController)?.clearDatabaseForSources()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }
    }

    private fun clearDatabaseForSources() {
        presenter.clearDatabaseForSources(selectedSources.map { it.source.id })
        actionFab!!.isVisible = false
        adapter?.clearSelection()
        adapter?.notifyDataSetChanged()
        activity?.toast(R.string.clear_database_completed)
    }
}
