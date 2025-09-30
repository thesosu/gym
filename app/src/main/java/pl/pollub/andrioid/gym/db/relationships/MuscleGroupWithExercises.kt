package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.MuscleGroup

data class MuscleGroupWithExercises (
    @Embedded val muscleGroup: MuscleGroup,
    @Relation(
        parentColumn = "muscle_group_id",
        entityColumn = "exercise_id",
        associateBy = Junction(ExerciseMuscleGroup::class)
    )
    val exercises: List<Exercise>
)

