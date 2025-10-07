package pl.pollub.andrioid.gym.network

import pl.pollub.andrioid.gym.network.dto.reguest.ChangePasswordRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ChangeUsernameRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ExerciseDto
import pl.pollub.andrioid.gym.network.dto.response.ExerciseResponse
import pl.pollub.andrioid.gym.network.dto.reguest.GoogleLoginRequest
import pl.pollub.andrioid.gym.network.dto.reguest.LoginRequest
import pl.pollub.andrioid.gym.network.dto.response.LoginResponse
import pl.pollub.andrioid.gym.network.dto.response.LogoutResponse
import pl.pollub.andrioid.gym.network.dto.response.RefreshResponse
import pl.pollub.andrioid.gym.network.dto.reguest.RegisterRequest
import pl.pollub.andrioid.gym.network.dto.reguest.VerifyEmailRequest
import pl.pollub.andrioid.gym.network.dto.response.ChangePasswordResponse
import pl.pollub.andrioid.gym.network.dto.response.ChangeUsernameResponse
import pl.pollub.andrioid.gym.network.dto.response.RegisterResponse
import pl.pollub.andrioid.gym.network.dto.response.VerifyEmailResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/logout")
    suspend fun logout(): LogoutResponse
    @POST("auth/refresh")
    fun refreshToken( ): Call<RefreshResponse>

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): LoginResponse

    @PATCH("users/me/password")
    suspend fun changeUserPassword(@Body request: ChangePasswordRequest): ChangePasswordResponse

    @PATCH("users/me/password")
    suspend fun changeUserName(@Body request: ChangeUsernameRequest): ChangeUsernameResponse

    @GET("auth/verify-email")
    suspend fun getEmailVerificationCode()

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): VerifyEmailResponse

    @POST("auth/verify-reset-code")
    suspend fun verifyResetPasswordCode(@Body request: VerifyResetCodeRequest): VerifyResetCodeResponse



    @POST("exercises")
    suspend fun insertExercise(@Body exercise: ExerciseDto):ExerciseResponse

}