package pl.pollub.andrioid.gym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueue (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "sync_queue_id") val syncQueueId: Int = 0,
    @ColumnInfo(name = "table_name") val tableName: String,
    @ColumnInfo(name = "local_id") val localId:Int,
    @ColumnInfo(name = "global_id") val globalId: Int? = null
)