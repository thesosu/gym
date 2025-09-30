package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.entity.Workout

data class UserWithWorkouts(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val workouts: List<Workout>
)
