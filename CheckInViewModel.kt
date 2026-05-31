package com.valentinesgarage.ui.checkin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.Truck
import com.valentinesgarage.data.repository.GarageRepository
import kotlinx.coroutines.launch

/**
 * CheckInViewModel
 *
 * Handles the logic for checking a truck into the garage.
 * The Fragment sends form data here; this class validates and saves it.
 */
class CheckInViewModel : ViewModel() {

    private val repository = GarageRepository()

    // Emits the ID of the newly created truck document on success
    private val _checkInSuccess = MutableLiveData<String>()
    val checkInSuccess: LiveData<String> = _checkInSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Validates form inputs and saves the truck to Firestore.
     *
     * @param plate      Vehicle registration plate number
     * @param owner      Name of the vehicle owner
     * @param kmText     Odometer reading as raw text from the input field
     * @param condition  Description of the truck's condition at check-in
     */
    fun checkIn(plate: String, owner: String, kmText: String, condition: String) {

        // ── Validation ──────────────────────────────
        if (plate.isBlank()) {
            _errorMessage.value = "Please enter the plate number."
            return
        }
        if (owner.isBlank()) {
            _errorMessage.value = "Please enter the owner's name."
            return
        }
        val km = kmText.toLongOrNull()
        if (km == null || km < 0) {
            _errorMessage.value = "Please enter a valid km reading."
            return
        }
        if (condition.isBlank()) {
            _errorMessage.value = "Please describe the vehicle condition."
            return
        }

        // ── Save to Firestore ────────────────────────
        _isLoading.value = true

        val truck = Truck(
            plateNumber  = plate.uppercase(),
            ownerName    = owner,
            kmReading    = km,
            condition    = condition,
            checkedInBy  = repository.currentUserId() ?: "",
            checkedInAt  = System.currentTimeMillis(),
            status       = "in_progress"
        )

        viewModelScope.launch {
            try {
                val newId = repository.checkInTruck(truck)
                _checkInSuccess.postValue(newId)
            } catch (e: Exception) {
                _errorMessage.postValue("Check-in failed: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
