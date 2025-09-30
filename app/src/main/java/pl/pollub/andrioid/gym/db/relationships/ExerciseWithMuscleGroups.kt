package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.MuscleGroup

data class ExerciseWithMuscleGroups (
    @Embedded
    val exercise: Exercise,

    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "muscle_group_id",
        associateBy = Junction(ExerciseMuscleGroup::class)
    )
    val muscleGroups: List<MuscleGroup>
)

