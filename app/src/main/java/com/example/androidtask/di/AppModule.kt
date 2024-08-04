package com.example.androidtask.di

import android.content.Context
import androidx.room.Room
import com.example.androidtask.Room.TaskDatabase
import com.example.androidtask.Room.TaskDatabaseDao
import com.example.androidtask.network.TaskApi
import com.example.androidtask.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideProductApi(): TaskApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNotesDao(taskDatabase: TaskDatabase): TaskDatabaseDao
            = taskDatabase.taskDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): TaskDatabase
            = Room.databaseBuilder(
        context,
        TaskDatabase::class.java,
        "task_db")
        .fallbackToDestructiveMigration()
        .build()
}