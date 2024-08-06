package com.example.androidtask.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidtask.model.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDatabaseDao
}