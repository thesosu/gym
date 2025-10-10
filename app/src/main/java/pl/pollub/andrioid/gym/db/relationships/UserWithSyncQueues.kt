package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.entity.User

data class UserWithSyncQueues (
    @Embedded val user: User,
    @Relation(parentColumn = "user_id", entityColumn = "user_id")
    val syncQueues: List<SyncQueue>
)