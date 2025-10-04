package pl.pollub.andrioid.gym.network.Dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ExerciseDto(
    val id: Int? = null,
    val name: String,
    val description: String?,
    val muscleGroups: List<Int>
)