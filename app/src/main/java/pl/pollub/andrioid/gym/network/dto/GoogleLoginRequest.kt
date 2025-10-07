package pl.pollub.andrioid.gym.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleLoginRequest(
    @Json(name = "idToken")
    val idToken: String,
)
