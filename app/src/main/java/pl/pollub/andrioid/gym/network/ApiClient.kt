package pl.pollub.andrioid.gym.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

object ApiClient {

    private const val TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwiaWF0IjoxNzU5NTg1NjM5LCJleHAiOjE3NjIxNzc2Mzl9.SnpD1G7bxK-RVxYrLF-RYFsUkAGMPMtAsk4WAjXEzcY"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(TOKEN))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/api/")
        .client(client) // <-- podłączamy klienta z interceptor
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)
}
