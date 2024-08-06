package com.example.androidtask.screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.androidtask.model.AuthTokenProvider
import com.example.androidtask.model.Task
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.repository.AuthRepository
import com.example.androidtask.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val authRepository: AuthRepository,
    private val authTokenProvider: AuthTokenProvider
) : ViewModel() {

    private val _taskList = MutableStateFlow<List<TaskEntity>>(emptyList())
    val taskList = _taskList.asStateFlow()

    /*private val _loading = MutableLiveData(false)
    var loading: Flow<Boolean> = _loading.asFlow()*/
    private val _loading = MutableStateFlow(false)
    var loading = MutableStateFlow(false)


    init {
        viewModelScope.launch {
            getAccessToken()
            fetchTask()
        }
    }

    suspend fun getAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = authRepository.getAccessToken("365", "1")
            authTokenProvider.setToken(token)
            Log.d("TAG", "getAccessToken: ${token}")
        }
    }

    suspend fun fetchTask() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val taskEntities = repository.fetchTasks()
            repository.getAllTasks().distinctUntilChanged()
                .collect { listOfTasks ->
                    if (listOfTasks.isNullOrEmpty()) {
                        Log.d("Empty", ": Empty list")
                    }else {
                        loading.value = false
                        _taskList.value = listOfTasks
                    }

                }
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            repository.searchTasks(query)
                .map { listOfTasks ->
                    listOfTasks.distinctBy { it.task + it.title + it.description + it.colorCode }
                } // Ensure distinct tasks by concatenating task properties
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

/*
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = mutableStateListOf<TaskEntity>()
    val tasks: List<TaskEntity> get() = _tasks.toList()
    val task = _tasks.asFlow()

    */
/*private val _noteList = MutableStateFlow<List<Note>>(emptyList())
    val noteList = _noteList.asStateFlow()*//*


    init {
        viewModelScope.launch {
            fetchTasks()
        }
    }

    suspend fun fetchTasks() {
        try {
            val taskEntities = repository.fetchTasks()
            _tasks.clear()
            _tasks.addAll(taskEntities)
        } catch (e: Exception) {
            // Handle error
            _tasks.clear()
            _tasks.addAll(repository.getAllTasks())
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            _tasks.clear()
            _tasks.addAll(repository.searchTasks(query))
        }
    }
}*/

