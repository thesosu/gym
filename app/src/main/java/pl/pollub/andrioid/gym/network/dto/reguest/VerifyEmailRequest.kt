package pl.pollub.andrioid.gym.network.dto.reguest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyEmailRequest(
    @Json(name = "code")
    val code: String
)
