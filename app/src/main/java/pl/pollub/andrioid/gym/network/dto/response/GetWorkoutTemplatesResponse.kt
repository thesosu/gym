package pl.pollub.andrioid.gym.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class GetWorkoutTemplatesResponse (
    @Json(name = "has_more")
    val hasMore: Boolean,
    @Json(name = "date")
    val data: List<WorkoutTemplateResponse>
)
@JsonClass(generateAdapter = true)
data class WorkoutTemplateResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "exercises")
    val exercises: List<Int>

)