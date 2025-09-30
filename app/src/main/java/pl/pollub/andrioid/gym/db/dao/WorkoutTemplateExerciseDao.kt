package pl.pollub.andrioid.gym.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplateExercise

@Dao
interface WorkoutTemplateExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutTemplateExercise(workoutTemplateExercise: WorkoutTemplateExercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkoutTemplateExercises(workoutTemplateExercises: List<WorkoutTemplateExercise>)

}