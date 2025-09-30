package pl.pollub.andrioid.gym.repository;

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.dao.WorkoutDao
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.relationships.WorkoutWithWorkoutExercises

public class WorkoutRepository(context: Context):WorkoutDao {
    override suspend fun insertWorkout(workout: Workout): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertAllWorkouts(workouts: List<Workout>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkout(workout: Workout) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWorkout(workout: Workout) {
        TODO("Not yet implemented")
    }

    override fun getWorkoutById(id: Int): Flow<Workout> {
        TODO("Not yet implemented")
    }

    override fun getAllWorkouts(): Flow<List<Workout>> {
        TODO("Not yet implemented")
    }

    override fun getWorkoutWithExercises(id: Int): Flow<WorkoutWithWorkoutExercises> {
        TODO("Not yet implemented")
    }

    override fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithWorkoutExercises>> {
        TODO("Not yet implemented")
    }
}
