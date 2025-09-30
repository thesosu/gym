package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplateExercise

data class ExerciseWithWorkoutTemplates (
    @Embedded
    val exercise: Exercise,

    @Relation(
        parentColumn = "exercise_id", entityColumn = "workout_template_id", associateBy = Junction(
            WorkoutTemplateExercise::class
        )
    )
    val templates: List<WorkoutTemplate>
)

