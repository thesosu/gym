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
    tableName = "workout_templates",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")]
)
data class WorkoutTemplate (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "workout_template_id") val workoutTemplateId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "global_id") val globalId:Int? =null
)
data class WorkoutTemplateWithExercises (
    @Embedded val workoutTemplate: WorkoutTemplate,
    @Relation(
        parentColumn = "workout_template_id",
        entityColumn = "exercise_id",
        associateBy = Junction(
            WorkoutTemplateExercise::class
        )
    )
    val exercises: List<Exercise> = emptyList()
)
data class WorkoutTemplateWithWorkouts (
    @Embedded val workoutTemplate: WorkoutTemplate,
    @Relation(parentColumn = "workout_template_id", entityColumn = "workout_template_id")
    val workouts: List<Workout> = emptyList()
)