package pl.pollub.andrioid.gym.network

import pl.pollub.andrioid.gym.network.Dto.ExerciseRequest
import pl.pollub.andrioid.gym.network.Dto.ExerciseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("exercises")
    suspend fun insertExercise(@Body exercise: ExerciseRequest): ExerciseResponse
}