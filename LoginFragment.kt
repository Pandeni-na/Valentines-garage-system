package com.valentinesgarage.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.valentinesgarage.R
import com.valentinesgarage.databinding.FragmentLoginBinding

/**
 * LoginFragment
 *
 * Shows email + password fields.
 * After a successful login it routes to the correct screen based on the user's role:
 *   - admin       → AdminDashboardFragment
 *   - mechanic    → MechanicDashboardFragment
 *   - receptionist → CheckInFragment
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe loading state – show/hide the progress bar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

        // Observe login result – navigate based on role
        viewModel.loginResult.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                Toast.makeText(requireContext(), "User not found. Contact admin.", Toast.LENGTH_LONG).show()
                return@observe
            }

            val destination = when (user.role) {
                "admin"        -> R.id.action_login_to_adminDashboard
                "mechanic"     -> R.id.action_login_to_mechanicDashboard
                "receptionist" -> R.id.action_login_to_checkIn
                else           -> {
                    Toast.makeText(requireContext(), "Unknown role: ${user.role}", Toast.LENGTH_LONG).show()
                    return@observe
                }
            }
            findNavController().navigate(destination)
        }

        // Login button click
        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
