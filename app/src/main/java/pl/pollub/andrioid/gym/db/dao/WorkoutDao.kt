package pl.pollub.andrioid.gym.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.relationships.WorkoutExerciseWithSets
import pl.pollub.andrioid.gym.db.relationships.WorkoutWithWorkoutExercises

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkouts(workouts: List<Workout>): List<Long>

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
    @Query("DELETE FROM workouts WHERE global_id = :id")
    suspend fun deleteWorkoutByGlobalId(id: Int)

    @Query("SELECT * FROM workouts WHERE workout_id = :id")
    fun getWorkoutById(id: Int): Flow<Workout>

    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE workout_id = :id")
    fun getWorkoutWithExercisesById(id: Int): Flow<WorkoutWithWorkoutExercises>

    @Transaction
    @Query("SELECT * FROM workout_exercises WHERE workout_id = :id order by position")
    fun getExercisesWithSetsForWorkout(id: Int): List<WorkoutExerciseWithSets>

    @Transaction
    @Query("SELECT * FROM workouts")
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithWorkoutExercises>>
}