package com.valentinesgarage.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.valentinesgarage.data.model.Truck
import com.valentinesgarage.databinding.ItemVehicleReportBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * VehicleReportAdapter
 * Displays truck details for the admin vehicle-report screen.
 */
class VehicleReportAdapter : ListAdapter<Truck, VehicleReportAdapter.VehicleViewHolder>(DIFF_CALLBACK) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemVehicleReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VehicleViewHolder(
        private val binding: ItemVehicleReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(truck: Truck) {
            binding.tvPlate     .text = truck.plateNumber
            binding.tvOwner     .text = "Owner: ${truck.ownerName}"
            binding.tvKm        .text = "Km at check-in: ${truck.kmReading}"
            binding.tvCondition .text = "Condition: ${truck.condition}"
            binding.tvDate      .text = "Checked in: ${dateFormat.format(Date(truck.checkedInAt))}"
            binding.tvStatus    .text = if (truck.status == "completed") "✓ Completed" else "⏳ In progress"
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Truck>() {
            override fun areItemsTheSame(old: Truck, new: Truck) = old.id == new.id
            override fun areContentsTheSame(old: Truck, new: Truck) = old == new
        }
    }
}
