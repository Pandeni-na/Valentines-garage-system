package com.valentinesgarage.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.valentinesgarage.databinding.FragmentVehicleReportBinding

/**
 * VehicleReportFragment
 *
 * Displays a list of all trucks (past and present) with:
 *   - Plate number
 *   - Owner name
 *   - Km reading at check-in
 *   - Condition at check-in
 *   - Current status (in progress / completed)
 */
class VehicleReportFragment : Fragment() {

    private var _binding: FragmentVehicleReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: VehicleReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VehicleReportAdapter()
        binding.rvVehicles.apply {
            this.adapter  = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

        viewModel.trucks.observe(viewLifecycleOwner) { trucks ->
            adapter.submitList(trucks)
        }

        viewModel.loadAllTrucks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
