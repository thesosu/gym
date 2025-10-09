package pl.pollub.andrioid.gym.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.relationships.UserWithBodyMeasurements
import pl.pollub.andrioid.gym.db.relationships.UserWithExercises
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndMuscleGroups
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndSets
import pl.pollub.andrioid.gym.db.relationships.UserWithSyncQueues
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkoutTemplates
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkouts

@Dao
interface UserDao {

    @Query("SELECT last_sync FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLastSync(): String?

    @Query("SELECT user_id FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUserId(): Int?
    @Query("UPDATE users SET last_sync = :lastSync WHERE isLoggedIn = 1")
    suspend fun updateLastSync(lastSync: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<User>): List<Long>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserById(id: Int): Flow<User>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithWorkoutsById(id: Int): Flow<UserWithWorkouts>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithExercisesById(id: Int): Flow<UserWithExercises>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithWorkoutTemplatesById(id: Int): Flow<UserWithWorkoutTemplates>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithBodyMeasurementsById(id: Int): Flow<UserWithBodyMeasurements>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithSyncQueuesById(id: Int): UserWithSyncQueues

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithExercisesAndMuscleGroupsById(id: Int): Flow<UserWithExercisesAndMuscleGroups>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithExercisesAndSetsById(id: Int): Flow<UserWithExercisesAndSets>
}