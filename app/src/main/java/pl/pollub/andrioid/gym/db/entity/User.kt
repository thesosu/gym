package pl.pollub.andrioid.gym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") val userId: Int = 0,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "sex") val sex: Boolean,
    @ColumnInfo(name = "global_id") val globalId: Int? = null,
)

data class UserWithWorkouts(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val workouts: List<Workout> = emptyList()
)

data class UserWithExercises(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val exercises: List<Exercise> = emptyList()
)

data class UserWithWorkoutTemplates(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val workoutTemplates: List<WorkoutTemplate> = emptyList()
)

data class UserWithBodyMeasurements(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val bodyMeasurements: List<BodyMeasurement> = emptyList()
)

data class UserWithExercisesAndMuscleGroups(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val exercisesWithMuscleGroups: List<ExerciseWithMuscleGroups> = emptyList()

)

data class UserWithExercisesAndSets(
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val exercisesWithSets: List<WorkoutExerciseWithSets> = emptyList()
)