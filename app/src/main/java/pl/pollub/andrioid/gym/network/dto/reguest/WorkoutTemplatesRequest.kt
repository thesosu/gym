package pl.pollub.andrioid.gym.network.dto.reguest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkoutTemplatesRequest(
    @Json(name = "name")
    val name: String,
    @Json(name = "exercises")
    val exercises: List<Int>
)
