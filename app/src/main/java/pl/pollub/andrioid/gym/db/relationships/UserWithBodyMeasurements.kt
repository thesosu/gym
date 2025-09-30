package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement
import pl.pollub.andrioid.gym.db.entity.User


data class UserWithBodyMeasurements(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val bodyMeasurements: List<BodyMeasurement>
)
