package pl.pollub.andrioid.gym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")]
)
data class Exercise (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "exercise_id") val exerciseId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "global_id") val globalId:Int? =null

)
data class ExerciseWithMuscleGroups (
    @Embedded
    val exercise: Exercise,

    @Relation(
        parentColumn = "exercise_id", entityColumn = "muscle_group_id", associateBy = Junction(
            ExerciseMuscleGroup::class
        )
    )
    val muscleGroups: List<MuscleGroup>
)

data class ExerciseWithWorkoutExercise (
    @Embedded
    val exercise: Exercise,

    @Relation(parentColumn = "exercise_id", entityColumn = "exercise_id")
    val workoutExercises: List<WorkoutExercise>
)
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
