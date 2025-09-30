package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Set
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise

data class WorkoutExerciseWithSets (
    @Embedded val workoutExercise: WorkoutExercise,
    @Relation(parentColumn = "workout_exercise_id", entityColumn = "workout_exercise_id")
    val sets: List<Set>
)
