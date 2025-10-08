package pl.pollub.andrioid.gym.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetExercisesResponse(
    @Json(name = "has_more")
    val hasMore: Boolean,
    @Json(name = "date")
    val data: List<ExerciseDto>

)
@JsonClass(generateAdapter = true)
data class ExerciseDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "muscleGroups")
    val muscleGroups: List<Int>?
)
