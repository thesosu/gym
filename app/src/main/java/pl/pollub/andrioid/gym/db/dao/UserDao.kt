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
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkoutTemplates
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkouts

@Dao
interface UserDao {

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
    fun getUserWithWorkouts(id: Int): Flow<UserWithWorkouts>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithExercises(id: Int): Flow<UserWithExercises>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithWorkoutTemplates(id: Int): Flow<UserWithWorkoutTemplates>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithBodyMeasurements(id: Int): Flow<UserWithBodyMeasurements>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithExercisesAndMuscleGroups(id: Int): Flow<UserWithExercisesAndMuscleGroups>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithExercisesAndSets(id: Int): Flow<UserWithExercisesAndSets>
}