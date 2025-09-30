package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.entity.Workout


data class UserWithExercisesAndSets(
    @Embedded val user: User,
    @Relation(
        entity = Workout::class,
        parentColumn = "user_id",
        entityColumn = "user_id")
    val exercisesWithSets: List<WorkoutWithWorkoutExerciseAndSets>
)
