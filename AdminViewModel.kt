package com.valentinesgarage.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.ServiceTask
import com.valentinesgarage.data.model.Truck
import com.valentinesgarage.data.model.User
import com.valentinesgarage.data.repository.GarageRepository
import kotlinx.coroutines.launch

/**0
 * AdminViewModel
 *
 * Provides data for Valentine's admin / reporting screen.
 *
 * Reports shown:
 *   1. Full list of trucks with their check-in condition and km reading
 *   2. Per-mechanic task report (what each employee did)
 */
class AdminViewModel : ViewModel() {

    private val repository = GarageRepository()

    // ── Trucks ───────────────────────────────────
    private val _trucks = MutableLiveData<List<Truck>>()
    val trucks: LiveData<List<Truck>> = _trucks

    // ── Mechanic report ──────────────────────────
    // Map of mechanic name → list of tasks they completed
    private val _mechanicReport = MutableLiveData<Map<User, List<ServiceTask>>>()
    val mechanicReport: LiveData<Map<User, List<ServiceTask>>> = _mechanicReport

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /** Loads all trucks for the vehicle condition report. */
    fun loadAllTrucks() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _trucks.postValue(repository.getAllTrucks())
            } catch (e: Exception) {
                _errorMessage.postValue("Could not load trucks: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Builds the per-mechanic report.
     * For each mechanic in the system it fetches all tasks they worked on.
     */
    fun loadMechanicReport() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val mechanics = repository.getAllMechanics()
                val report    = mutableMapOf<User, List<ServiceTask>>()

                for (mechanic in mechanics) {
                    val tasks = repository.getTasksByMechanic(mechanic.uid)
                    report[mechanic] = tasks
                }
                _mechanicReport.postValue(report)
            } catch (e: Exception) {
                _errorMessage.postValue("Could not load report: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /** Signs Valentine out of the app. */
    fun signOut() = repository.signOut()
}
