package pl.pollub.andrioid.gym.network.Dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class MuscleGroupResponse (
    @Json(name = "id")
    val id: Int

)