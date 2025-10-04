package pl.pollub.andrioid.gym.network

import pl.pollub.andrioid.gym.network.Dto.ExerciseDto
import pl.pollub.andrioid.gym.network.Dto.ExerciseResponse
import pl.pollub.andrioid.gym.network.Dto.MuscleGroupDto
import pl.pollub.andrioid.gym.network.Dto.MuscleGroupResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("exercises")
    suspend fun insertExercise(@Body exercise: ExerciseDto):ExerciseResponse
    @POST("muscle_groups")
    suspend fun insertMuscleGroup(@Body muscleGroup: MuscleGroupDto):MuscleGroupResponse
}