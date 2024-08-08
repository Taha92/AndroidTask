package com.example.androidtask.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.androidtask.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: TaskRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {

            Log.d("RefreshDataWorker", "doWork: ")
            repository.fetchTasks()
            Result.success()
        } catch (e: Exception) {
            Log.d("TAG", "doWork: ${e.message.toString()}")
            Result.failure()
        }
    }
}
