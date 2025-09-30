package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate

data class UserWithWorkoutTemplates(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val workoutTemplates: List<WorkoutTemplate>
)
