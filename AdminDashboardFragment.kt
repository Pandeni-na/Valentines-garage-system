package com.valentinesgarage.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.valentinesgarage.R
import com.valentinesgarage.databinding.FragmentAdminDashboardBinding

/**
 * AdminDashboardFragment
 *
 * Valentine's home screen. Shows two big buttons:
 *   1. "Vehicle Reports"   → lists all checked-in trucks + their condition
 *   2. "Employee Reports"  → shows what each mechanic did
 *
 * Also has a Sign Out option in the toolbar.
 */
class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVehicleReport  .setOnClickListener {
            findNavController().navigate(R.id.action_admin_to_vehicleReport)
        }
        binding.btnEmployeeReport .setOnClickListener {
            findNavController().navigate(R.id.action_admin_to_employeeReport)
        }
        binding.btnSignOut.setOnClickListener {
            viewModel.signOut()
            findNavController().navigate(R.id.action_admin_to_login)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
