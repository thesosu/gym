package pl.pollub.andrioid.gym.network

import pl.pollub.andrioid.gym.network.dto.ExerciseDto
import pl.pollub.andrioid.gym.network.dto.ExerciseResponse
import pl.pollub.andrioid.gym.network.dto.GoogleLoginRequest
import pl.pollub.andrioid.gym.network.dto.LoginRequest
import pl.pollub.andrioid.gym.network.dto.LoginResponse
import pl.pollub.andrioid.gym.network.dto.RefreshResponse
import pl.pollub.andrioid.gym.network.dto.RegisterRequest
import pl.pollub.andrioid.gym.network.dto.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/refresh")
    fun refreshToken( ): Call<RefreshResponse>

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest)

    @PATCH("users/me/password")
    suspend fun changeUserPassword()

    @POST("exercises")
    suspend fun insertExercise(@Body exercise: ExerciseDto):ExerciseResponse

}