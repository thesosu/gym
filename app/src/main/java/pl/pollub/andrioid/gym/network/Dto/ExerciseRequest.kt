package pl.pollub.andrioid.gym.network.Dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ExerciseRequest(
    val userId: Int,
    val name: String,
    val description: String
)