package pl.pollub.andrioid.gym.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseWithMuscleGroups
import pl.pollub.andrioid.gym.db.entity.ExerciseWithWorkoutExercise
import pl.pollub.andrioid.gym.db.entity.ExerciseWithWorkoutTemplates

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>): List<Long>

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT * FROM exercises WHERE exercise_id = :id")
    fun getExerciseById(id: Int): Flow<Exercise>

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE exercise_id = :id")
    fun getExerciseWithMuscleGroups(id: Int): Flow<ExerciseWithMuscleGroups>

    @Transaction
    @Query("SELECT * FROM exercises WHERE exercise_id = :id")
    fun getExerciseWithWorkoutExercises(id: Int): Flow<ExerciseWithWorkoutExercise>

    @Transaction
    @Query("SELECT * FROM exercises WHERE exercise_id = :id")
    fun getExerciseWithWorkoutTemplates(id: Int): Flow<ExerciseWithWorkoutTemplates>

    @Transaction
    @Query("SELECT * FROM exercises")
    fun getAllExercisesWithMuscleGroups(): Flow<List<ExerciseWithMuscleGroups>>

    @Transaction
    @Query("SELECT * FROM exercises")
    fun getAllExercisesWithWorkoutExercises(): Flow<List<ExerciseWithWorkoutExercise>>

    @Transaction
    @Query("SELECT * FROM exercises")
    fun getAllExercisesWithWorkoutTemplates(): Flow<List<ExerciseWithWorkoutTemplates>>
}