package pl.pollub.andrioid.gym.network

import pl.pollub.andrioid.gym.network.Dto.ExerciseDto
import pl.pollub.andrioid.gym.network.Dto.ExerciseResponse
import pl.pollub.andrioid.gym.network.Dto.LoginRequest
import pl.pollub.andrioid.gym.network.Dto.LoginResponse
import pl.pollub.andrioid.gym.network.Dto.MuscleGroupDto
import pl.pollub.andrioid.gym.network.Dto.MuscleGroupResponse
import pl.pollub.andrioid.gym.network.Dto.RegisterRequest
import pl.pollub.andrioid.gym.network.Dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("exercises")
    suspend fun insertExercise(@Body exercise: ExerciseDto):ExerciseResponse
    @POST("muscle_groups")
    suspend fun insertMuscleGroup(@Body muscleGroup: MuscleGroupDto):MuscleGroupResponse
}