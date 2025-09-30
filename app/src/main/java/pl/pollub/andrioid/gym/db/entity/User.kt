package pl.pollub.andrioid.gym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") val userId: Int = 0,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "sex") val sex: Boolean,
    @ColumnInfo(name = "global_id") val globalId: Int? = null,
)

