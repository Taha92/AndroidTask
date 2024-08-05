package com.example.androidtask.connection

import com.example.androidtask.model.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val authTokenProvider: AuthTokenProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer ${authTokenProvider.getToken()}")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}