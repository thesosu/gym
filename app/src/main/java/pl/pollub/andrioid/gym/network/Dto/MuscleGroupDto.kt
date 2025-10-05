package pl.pollub.andrioid.gym.network.Dto

import com.squareup.moshi.Json

data class MuscleGroupDto(
    @Json(name = "id")
    val id: Int? = null,
    @Json(name = "name")
    val name: String
)
