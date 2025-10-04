package pl.pollub.andrioid.gym.network.Dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExerciseResponse(
    val id: Int
)

