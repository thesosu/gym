package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplateExercise

data class WorkoutTemplateWithExercises (
    @Embedded val workoutTemplate: WorkoutTemplate,
    @Relation(
        parentColumn = "workout_template_id",
        entityColumn = "exercise_id",
        associateBy = Junction(
            WorkoutTemplateExercise::class
        )
    )
    val exercises: List<Exercise>
)