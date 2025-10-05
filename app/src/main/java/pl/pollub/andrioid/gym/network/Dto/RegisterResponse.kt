package pl.pollub.andrioid.gym.network.Dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "message")
    val message: String
)