package pl.pollub.andrioid.gym.network.dto.response

data class BodyMeasurementResponse(
    val id: Int,
    val deleted: Boolean,
    val weight: Float?,
    val arm: Float?,
    val forearm: Float?,
    val chest: Float?,
    val waist: Float?,
    val hips: Float?,
    val thigh: Float?,
    val calf: Float?,
    val date: String
)

data class GetBodyMeasurementsResponse(
    val has_more: Boolean,
    val data: List<BodyMeasurementResponse>

)
