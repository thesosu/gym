package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise

data class ExerciseWithWorkoutExercise (
    @Embedded
    val exercise: Exercise,

    @Relation(parentColumn = "exercise_id", entityColumn = "exercise_id")
    val workoutExercises: List<WorkoutExercise>
)
