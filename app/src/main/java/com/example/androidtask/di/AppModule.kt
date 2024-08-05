package com.example.androidtask.di

import android.content.Context
import androidx.room.Room
import com.example.androidtask.Room.TaskDatabase
import com.example.androidtask.Room.TaskDatabaseDao
import com.example.androidtask.connection.HeaderInterceptor
import com.example.androidtask.model.AuthTokenProvider
import com.example.androidtask.network.TaskApi
import com.example.androidtask.repository.AuthRepository
import com.example.androidtask.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            val builder = OkHttpClient.Builder()
            builder.readTimeout(36000L, TimeUnit.MILLISECONDS)
            builder.connectTimeout(36000L, TimeUnit.MILLISECONDS)
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideProductApi(okHttpClient: OkHttpClient): TaskApi {
        return Retrofit.Builder()
            .client(okHttpClient)
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

    @Provides
    @Singleton
    fun provideAuthRepository(authService: TaskApi): AuthRepository {
        return AuthRepository(authService)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authTokenProvider: AuthTokenProvider): HeaderInterceptor {
        return HeaderInterceptor(authTokenProvider)
    }
}