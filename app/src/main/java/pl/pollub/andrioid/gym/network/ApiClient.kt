package pl.pollub.andrioid.gym.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()

        val request = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(request.build())
    }
}

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    fun create(context: Context): ApiService {
        val tokenManager = TokenManager(context)
        val cookieJar = RefreshCookieJar(context)

        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager, cookieJar))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
