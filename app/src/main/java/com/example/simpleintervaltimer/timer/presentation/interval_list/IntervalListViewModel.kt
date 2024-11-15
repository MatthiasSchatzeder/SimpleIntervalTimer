package com.example.simpleintervaltimer.timer.presentation.interval_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import com.example.simpleintervaltimer.timer.data.repositories.StoredTimeIntervalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntervalListViewModelFactory(
    private val storedTimeIntervalRepository: StoredTimeIntervalRepository = StoredTimeIntervalRepository()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = IntervalListViewModel(storedTimeIntervalRepository) as T
}

class IntervalListViewModel(
    private val storedTimeIntervalRepository: StoredTimeIntervalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState(isLoading = true))
    val uiState = _uiState
        .onStart { startCollectFromRepository() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState(isLoading = true))

    private fun startCollectFromRepository() = viewModelScope.launch {
        storedTimeIntervalRepository.storedTimeIntervalsFlow.collect { storedTimeIntervals ->
            _uiState.value = _uiState.value.copy(isLoading = false, storedTimeIntervals = storedTimeIntervals)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val storedTimeIntervals: List<StoredTimeInterval> = emptyList(),
        val storedTimeIntervalToDelete: StoredTimeInterval? = null
    )

    fun setStoredTimeIntervalToDelete(storedTimeInterval: StoredTimeInterval?) {
        _uiState.value = _uiState.value.copy(storedTimeIntervalToDelete = storedTimeInterval)
    }

    fun deleteStoredTimeInterval(storedTimeInterval: StoredTimeInterval) {
        viewModelScope.launch {
            storedTimeIntervalRepository.deleteStoredTimeInterval(storedTimeInterval)
        }
    }
}
