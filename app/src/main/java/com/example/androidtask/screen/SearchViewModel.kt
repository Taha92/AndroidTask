package com.example.androidtask.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: TaskRepository,
) : ViewModel() {

    private val _taskList = MutableStateFlow<List<TaskEntity>>(emptyList())
    val taskList = _taskList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    var loading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            searchTasks("")
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            if(query.isNotEmpty()) {
                repository.searchTasks(query)
                    .map { listOfTasks ->
                        listOfTasks.distinctBy { it.task + it.title + it.description + it.colorCode + it.wageType + it.businessUnit }
                    }
                    .distinctUntilChanged() // Emit only when the list changes
                    .collect { listOfTasks ->
                        if (listOfTasks.isNullOrEmpty()) {
                            Log.d("Empty", ": Empty list")
                        } else {
                            _taskList.value = listOfTasks
                        }
                    }
            }
        }
    }

    fun clearTasks() {
        _taskList.value = emptyList()
    }}