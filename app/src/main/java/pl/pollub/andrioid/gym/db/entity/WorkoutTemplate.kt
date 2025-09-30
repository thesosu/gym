package pl.pollub.andrioid.gym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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