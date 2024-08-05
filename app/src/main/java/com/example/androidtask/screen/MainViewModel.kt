package com.example.androidtask.screen

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtask.model.AuthTokenProvider
import com.example.androidtask.model.Task
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.repository.AuthRepository
import com.example.androidtask.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
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
            val taskEntities = repository.fetchTasks()
            /*_taskList
            taskList.addAll(taskEntities)*/
            repository.getAllTasks().distinctUntilChanged()
                .collect { listOfTasks ->
                    if (listOfTasks.isNullOrEmpty()) {
                        Log.d("Empty", ": Empty list")
                    }else {
                        _taskList.value = listOfTasks
                    }

                }
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            /*_taskList.collectAsState().value
            _tasks.addAll(repository.searchTasks(query))*/

            repository.searchTasks(query).distinctUntilChanged()
                .collect { listOfTasks ->
                    if (listOfTasks.isNullOrEmpty()) {
                        Log.d("Empty", ": Empty list")
                    }else {
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

