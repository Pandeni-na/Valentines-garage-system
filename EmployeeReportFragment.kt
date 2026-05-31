package com.valentinesgarage.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.valentinesgarage.databinding.FragmentEmployeeReportBinding

/**
 * EmployeeReportFragment
 *
 * Shows Valentine a breakdown of what each mechanic did.
 * For each mechanic it lists every task they touched, with notes.
 */
class EmployeeReportFragment : Fragment() {

    private var _binding: FragmentEmployeeReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: EmployeeReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EmployeeReportAdapter()
        binding.rvReport.apply {
            this.adapter  = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

        viewModel.mechanicReport.observe(viewLifecycleOwner) { report ->
            // Flatten the map into a list of display items for the adapter
            val items = mutableListOf<EmployeeReportItem>()
            report.forEach { (mechanic, tasks) ->
                items.add(EmployeeReportItem.Header(mechanic.name))
                if (tasks.isEmpty()) {
                    items.add(EmployeeReportItem.EmptyRow)
                } else {
                    tasks.forEach { task ->
                        items.add(EmployeeReportItem.TaskRow(task))
                    }
                }
            }
            adapter.submitList(items)
        }

        viewModel.loadMechanicReport()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
