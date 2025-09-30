package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise

data class WorkoutWithWorkoutExercises (
    @Embedded val workout: Workout,
    @Relation(parentColumn = "workout_id", entityColumn = "workout_id")
    var workoutExercises: List<WorkoutExercise>
)
