package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.User


data class UserWithExercisesAndMuscleGroups(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val exercisesWithMuscleGroups: List<ExerciseWithMuscleGroups>

)
