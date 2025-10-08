package pl.pollub.andrioid.gym.network.dto.reguest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkoutRequest(
    @Json(name = "duration")
    val duration: Int? = null,
    @Json(name = "template")
    val template: Int? = null,
    @Json(name = "date")
    val date: String,
    @Json(name = "exercises")
    val exercises: List<WorkoutExerciseDto>
)

@JsonClass(generateAdapter = true)
data class WorkoutExerciseDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "sets")
    val sets: List<WorkoutSetDto>
)
@JsonClass(generateAdapter = true)
data class WorkoutSetDto(
    @Json(name = "reps")
    val reps: Int,
    @Json(name = "weight")
    val weight: Double
)

