package pl.pollub.andrioid.gym.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "token")
    val token: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "last_sync")
    val lastSync: String
)