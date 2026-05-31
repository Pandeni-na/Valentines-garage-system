package com.valentinesgarage.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.valentinesgarage.data.model.User
import com.valentinesgarage.data.repository.GarageRepository
import kotlinx.coroutines.launch

/**
 * LoginViewModel
 *
 * Handles sign-in logic and exposes the result through LiveData.
 * The Fragment observes loginResult to react to success or failure.
 */
class LoginViewModel : ViewModel() {

    private val auth       = FirebaseAuth.getInstance()
    private val repository = GarageRepository()

    // Holds either a User (success) or null (failure)
    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    // Holds an error message to show in the UI
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // True while a sign-in request is in flight
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Attempts to sign in with the given email and password.
     * On success it fetches the user's profile from Firestore and
     * posts it to loginResult so the Fragment can navigate by role.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter your email and password."
            return
        }

        _isLoading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                // Fetch the user's role from Firestore
                viewModelScope.launch {
                    val user = repository.getUser(uid)
                    _isLoading.postValue(false)
                    _loginResult.postValue(user)
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Login failed."
            }
    }
}
