package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate

data class WorkoutTemplateWithWorkouts (
    @Embedded val workoutTemplate: WorkoutTemplate,
    @Relation(parentColumn = "workout_template_id", entityColumn = "workout_template_id")
    val workouts: List<Workout>
)