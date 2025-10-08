package pl.pollub.andrioid.gym.network

import pl.pollub.andrioid.gym.network.dto.reguest.AddEmailRequest
import pl.pollub.andrioid.gym.network.dto.reguest.BodyMeasurementRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ChangePasswordRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ChangeUsernameRequest
import pl.pollub.andrioid.gym.network.dto.reguest.DeleteEmailRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ExerciseRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ForgotPasswordRequest
import pl.pollub.andrioid.gym.network.dto.reguest.GoogleLoginRequest
import pl.pollub.andrioid.gym.network.dto.reguest.LoginRequest
import pl.pollub.andrioid.gym.network.dto.response.LoginResponse
import pl.pollub.andrioid.gym.network.dto.response.LogoutResponse
import pl.pollub.andrioid.gym.network.dto.response.RefreshResponse
import pl.pollub.andrioid.gym.network.dto.reguest.RegisterRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ResetPasswordRequest
import pl.pollub.andrioid.gym.network.dto.reguest.VerifyEmailRequest
import pl.pollub.andrioid.gym.network.dto.reguest.VerifyResetCodeRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutTemplatesRequest
import pl.pollub.andrioid.gym.network.dto.response.AddEmailResponse
import pl.pollub.andrioid.gym.network.dto.response.AddResponse
import pl.pollub.andrioid.gym.network.dto.response.UpdateResponse
import pl.pollub.andrioid.gym.network.dto.response.ChangePasswordResponse
import pl.pollub.andrioid.gym.network.dto.response.ChangeUsernameResponse
import pl.pollub.andrioid.gym.network.dto.response.DeleteEmailResponse
import pl.pollub.andrioid.gym.network.dto.response.DeleteResponse
import pl.pollub.andrioid.gym.network.dto.response.ForgotPasswordResponse
import pl.pollub.andrioid.gym.network.dto.response.GetBodyMeasurementsResponse
import pl.pollub.andrioid.gym.network.dto.response.GetExercisesResponse
import pl.pollub.andrioid.gym.network.dto.response.GetWorkoutTemplatesResponse
import pl.pollub.andrioid.gym.network.dto.response.GetWorkoutsResponse
import pl.pollub.andrioid.gym.network.dto.response.RegisterResponse
import pl.pollub.andrioid.gym.network.dto.response.ResetPasswordResponse
import pl.pollub.andrioid.gym.network.dto.response.UserLastSyncResponse
import pl.pollub.andrioid.gym.network.dto.response.VerifyEmailResponse
import pl.pollub.andrioid.gym.network.dto.response.VerifyResetCodeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun verifyResetPasswordCode(@Body request: VerifyResetCodeRequest): VerifyResetCodeResponse //pomyśleć jak zapisywać token

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @PATCH("users/me/email")
    suspend fun addEmail(@Body request: AddEmailRequest): AddEmailResponse

    @DELETE("users/me/email")
    suspend fun deleteUserEmail(@Body request: DeleteEmailRequest): DeleteEmailResponse


    @GET("users/me/last-sync")
    suspend fun getUserLastSync(): UserLastSyncResponse

    //========================================= Sync =====================================
    //========================================= bodyMeasurements =====================================

    @POST("bodyMeasurements")
    suspend fun addBodyMeasurement(
        @Body request: BodyMeasurementRequest,
        @Header("X-Last-Sync") lastSync: String
    ): AddResponse

    @PUT("bodyMeasurements/{id}")
    suspend fun updateBodyMeasurement(
        @Path("id") id: Int,
        @Body request: BodyMeasurementRequest,
        @Header("X-Last-Sync") lastSync: String
    ): UpdateResponse

    @DELETE("bodyMeasurements/{id}")
    suspend fun deleteBodyMeasurement(
        @Path("id") id: Int,
        @Header("X-Last-Sync") lastSync: String
    ): DeleteResponse

    @GET("bodyMeasurements")
    suspend fun getUserMeasurements(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String
    ): List<GetBodyMeasurementsResponse>

    //========================================= exercises =====================================
    @POST("exercises")
    suspend fun addExercise(
        @Body request: ExerciseRequest,
        @Header("X-Last-Sync") lastSync: String
    ): AddResponse

    @PUT("exercises/{id}")
    suspend fun updateExercise(
        @Path("id") id: Int,
        @Body request: ExerciseRequest,
        @Header("X-Last-Sync") lastSync: String
    ): UpdateResponse

    @DELETE("exercises/{id}")
    suspend fun deleteExercise(
        @Path("id") id: Int,
        @Header("X-Last-Sync") lastSync: String
    ): DeleteResponse

    @GET("exercises")
    suspend fun getExercises(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String
    ): List<GetExercisesResponse>



    //========================================= workoutTemplate =====================================
    @POST("workoutTemplates")
    suspend fun addWorkoutTemplate(
        @Body request: WorkoutTemplatesRequest,
        @Header("X-Last-Sync") lastSync: String
    ): AddResponse

    @PUT("workoutTemplates/{id}")
    suspend fun updateWorkoutTemplate(
        @Path("id") id: Int,
        @Body request: WorkoutTemplatesRequest,
        @Header("X-Last-Sync") lastSync: String
    ): UpdateResponse

    @DELETE("workoutTemplates/{id}")
    suspend fun deleteWorkoutTemplate(
        @Path("id") id: Int,
        @Header("X-Last-Sync") lastSync: String
    ): DeleteResponse

    @GET("workoutTemplates")
    suspend fun getUserWorkoutTemplates(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("startDate") startDate: String? = null,  //ofset limit itd
        @Query("endDate") endDate: String
    ): List<GetWorkoutTemplatesResponse>

    //========================================= workout =====================================
    @POST("workouts")
    suspend fun addWorkout(
        @Body request: WorkoutRequest,
        @Header("X-Last-Sync") lastSync: String
    ): AddResponse

    @PUT("workouts/{id}")
    suspend fun updateWorkout(
        @Path("id") id: Int,
        @Body request: WorkoutRequest,
        @Header("X-Last-Sync") lastSync: String
    ): UpdateResponse

    @DELETE("workouts/{id}")
    suspend fun deleteWorkout(
        @Path("id") id: Int,
        @Header("X-Last-Sync") lastSync: String
    ): DeleteResponse

    @GET("workouts")
    suspend fun getUserWorkouts(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 5,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String
    ): List<GetWorkoutsResponse>



}