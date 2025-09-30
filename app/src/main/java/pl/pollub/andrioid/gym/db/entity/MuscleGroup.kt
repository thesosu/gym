package pl.pollub.andrioid.gym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "muscle_groups")
data class MuscleGroup(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "muscle_group_id") val muscleGroupId: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "global_id") val globalId:Int? = null
)
data class MuscleGroupWithExercises (
    @Embedded val muscleGroup: MuscleGroup,
    @Relation(
        parentColumn = "muscle_group_id",
        entityColumn = "exercise_id",
        associateBy = Junction(ExerciseMuscleGroup::class)
    )
    val exercises: List<Exercise> = emptyList()
)
