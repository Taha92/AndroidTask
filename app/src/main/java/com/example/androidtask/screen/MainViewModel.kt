package com.example.androidtask.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtask.model.AuthTokenProvider
import com.example.androidtask.model.TaskEntity
import com.example.androidtask.repository.AuthRepository
import com.example.androidtask.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            Log.d("TAG", "getAccessToken: $token")
        }
    }

    suspend fun fetchTask() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val taskEntities = repository.fetchTasks()
            repository.getAllTasks().distinctUntilChanged()
                .collect { listOfTasks ->
                    if (listOfTasks.isEmpty()) {
                        Log.d("Empty", ": Empty list")
                    }else {
                        loading.value = false
                        _taskList.value = listOfTasks
                    }

                }
        }
    }
}


