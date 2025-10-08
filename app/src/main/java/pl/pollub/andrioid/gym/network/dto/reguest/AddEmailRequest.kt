package pl.pollub.andrioid.gym.network.dto.reguest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AddEmailRequest(
    @Json(name = "email")
    val email: String
)
