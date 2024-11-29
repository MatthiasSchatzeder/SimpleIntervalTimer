package com.example.simpleintervaltimer.timer.presentation.edit_interval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import com.example.simpleintervaltimer.timer.data.repositories.StoredTimeIntervalRepository
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class EditIntervalViewModelFactory(
    private val storedTimeIntervalIdHexString: String,
    private val storedTimeIntervalRepository: StoredTimeIntervalRepository = StoredTimeIntervalRepository()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EditIntervalViewModel(storedTimeIntervalIdHexString, storedTimeIntervalRepository) as T
}

class EditIntervalViewModel(
    storedTimeIntervalIdHexString: String,
    private val storedTimeIntervalRepository: StoredTimeIntervalRepository
) : ViewModel() {
    private lateinit var persistedStoredTimeInterval: StoredTimeInterval

    private val _uiState = MutableStateFlow(UiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val storedTimeIntervalId = ObjectId(storedTimeIntervalIdHexString)
            val storedTimeInterval = storedTimeIntervalRepository.getStoredTimeInterval(storedTimeIntervalId)
            if (storedTimeInterval == null) {
                throw IllegalStateException("Could not load time interval to edit")
            }
            persistedStoredTimeInterval = storedTimeInterval
            _uiState.value = UiState(
                name = storedTimeInterval.name,
                timeInterval = TimeInterval(storedTimeInterval.workTime, storedTimeInterval.restTime, storedTimeInterval.intervals)
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val name: String? = null,
        val timeInterval: TimeInterval? = null,
        val showDiscardChangesDialog: Boolean = false
    )

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onTimeIntervalChanged(timeInterval: TimeInterval) {
        _uiState.value = _uiState.value.copy(timeInterval = timeInterval)
    }

    fun dismissDiscardChangesDialog() {
        _uiState.value = _uiState.value.copy(showDiscardChangesDialog = false)
    }

    fun cancelEdit(onEditFinished: () -> Unit) {
        if (!hasInputChanged()) {
            onEditFinished()
            return
        }
        _uiState.value = _uiState.value.copy(showDiscardChangesDialog = true)
    }

    fun saveChanges(onEditFinished: () -> Unit) {
        if (!hasInputChanged()) {
            onEditFinished()
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val timeInterval = _uiState.value.timeInterval ?: return@launch
            val name = _uiState.value.name ?: return@launch
            val toUpdate = StoredTimeInterval().apply {
                this._id = persistedStoredTimeInterval._id
                this.name = name
                this.workTime = timeInterval.workTime
                this.restTime = timeInterval.restTime
                this.intervals = timeInterval.intervals
            }
            storedTimeIntervalRepository.updateStoredTimeInterval(toUpdate)
            persistedStoredTimeInterval = toUpdate
            _uiState.value = _uiState.value.copy(isLoading = false)
            onEditFinished()
        }
    }

    private fun hasInputChanged(): Boolean {
        val uiState = _uiState.value
        if (uiState.name == null || uiState.timeInterval == null) {
            throw IllegalStateException("Could not compare if the input has changed")
        }
        if (persistedStoredTimeInterval.name != uiState.name) return true
        val timeIntervalFromStoredTimeInterval = TimeInterval(
            persistedStoredTimeInterval.workTime,
            persistedStoredTimeInterval.restTime,
            persistedStoredTimeInterval.intervals
        )
        return timeIntervalFromStoredTimeInterval != uiState.timeInterval
    }
}
