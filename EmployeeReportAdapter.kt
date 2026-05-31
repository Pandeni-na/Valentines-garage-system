package com.valentinesgarage.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.valentinesgarage.data.model.ServiceTask
import com.valentinesgarage.databinding.ItemEmployeeHeaderBinding
import com.valentinesgarage.databinding.ItemEmployeeTaskBinding

/**
 * EmployeeReportItem
 *
 * Sealed class representing the two types of rows in the employee report list:
 *  - Header: shows the mechanic's name
 *  - TaskRow: shows one task the mechanic worked on
 *  - EmptyRow: placeholder when a mechanic has no tasks
 */
sealed class EmployeeReportItem {
    data class Header  (val mechanicName: String) : EmployeeReportItem()
    data class TaskRow (val task: ServiceTask)    : EmployeeReportItem()
    object EmptyRow                               : EmployeeReportItem()
}

/**
 * EmployeeReportAdapter
 *
 * Multi-viewtype adapter for the admin employee report screen.
 */
class EmployeeReportAdapter :
    ListAdapter<EmployeeReportItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val VIEW_HEADER = 0
        private const val VIEW_TASK   = 1
        private const val VIEW_EMPTY  = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EmployeeReportItem>() {
            override fun areItemsTheSame(old: EmployeeReportItem, new: EmployeeReportItem): Boolean {
                return when {
                    old is EmployeeReportItem.Header  && new is EmployeeReportItem.Header  -> old.mechanicName == new.mechanicName
                    old is EmployeeReportItem.TaskRow && new is EmployeeReportItem.TaskRow -> old.task.id == new.task.id
                    old is EmployeeReportItem.EmptyRow && new is EmployeeReportItem.EmptyRow -> true
                    else -> false
                }
            }
            override fun areContentsTheSame(old: EmployeeReportItem, new: EmployeeReportItem) = old == new
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is EmployeeReportItem.Header  -> VIEW_HEADER
        is EmployeeReportItem.TaskRow -> VIEW_TASK
        is EmployeeReportItem.EmptyRow -> VIEW_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_HEADER -> HeaderViewHolder(
                ItemEmployeeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_TASK   -> TaskViewHolder(
                ItemEmployeeTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else        -> EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is EmployeeReportItem.Header  -> (holder as HeaderViewHolder).bind(item)
            is EmployeeReportItem.TaskRow -> (holder as TaskViewHolder).bind(item)
            else -> { /* empty row – nothing to bind */ }
        }
    }

    // ── ViewHolders ──────────────────────────────

    class HeaderViewHolder(private val binding: ItemEmployeeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmployeeReportItem.Header) {
            binding.tvMechanicName.text = item.mechanicName
        }
    }

    class TaskViewHolder(private val binding: ItemEmployeeTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmployeeReportItem.TaskRow) {
            binding.tvTaskTitle .text = item.task.title
            binding.tvTaskNotes .text = if (item.task.notes.isBlank()) "No notes" else item.task.notes
            binding.tvDoneStatus.text = if (item.task.isDone) "✓ Done" else "✗ Pending"
        }
    }

    class EmptyViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)
}
