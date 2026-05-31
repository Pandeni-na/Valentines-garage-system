package com.valentinesgarage.ui.checkin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.valentinesgarage.databinding.FragmentCheckInBinding

/**
 * CheckInFragment
 *
 * Screen used by the Receptionist to log a truck into the garage.
 * Fields: plate number, owner name, km reading, vehicle condition.
 */
class CheckInFragment : Fragment() {

    private var _binding: FragmentCheckInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show/hide progress indicator
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCheckIn.isEnabled = !isLoading
        }

        // Show error messages as a Toast
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

        // On success: clear form and confirm to the user
        viewModel.checkInSuccess.observe(viewLifecycleOwner) { truckId ->
            Toast.makeText(
                requireContext(),
                "Truck checked in successfully! ID: $truckId",
                Toast.LENGTH_LONG
            ).show()
            clearForm()
        }

        // Submit button
        binding.btnCheckIn.setOnClickListener {
            viewModel.checkIn(
                plate     = binding.etPlateNumber.text.toString().trim(),
                owner     = binding.etOwnerName.text.toString().trim(),
                kmText    = binding.etKmReading.text.toString().trim(),
                condition = binding.etCondition.text.toString().trim()
            )
        }
    }

    /** Resets all input fields after a successful check-in. */
    private fun clearForm() {
        binding.etPlateNumber.text?.clear()
        binding.etOwnerName.text?.clear()
        binding.etKmReading.text?.clear()
        binding.etCondition.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
