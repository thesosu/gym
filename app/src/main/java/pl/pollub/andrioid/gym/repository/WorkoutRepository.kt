package pl.pollub.andrioid.gym.repository;

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.WorkoutDao
import pl.pollub.andrioid.gym.db.dao.WorkoutExerciseDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise
import pl.pollub.andrioid.gym.db.relationships.WorkoutExerciseWithSets
import pl.pollub.andrioid.gym.db.relationships.WorkoutWithWorkoutExercises

public class WorkoutRepository(context: Context):WorkoutDao, WorkoutExerciseDao {
    private val workoutDao = AppDb.getInstance(context).workoutDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    private val workoutExerciseDao = AppDb.getInstance(context).workoutExerciseDao()
    override suspend fun insertWorkout(workout: Workout): Long {
        val newId = workoutDao.insertWorkout(workout)

        val q = SyncQueue(
            tableName = "workouts",
            localId = newId.toInt()
        )
        syncQueueDao.insertSyncQueue(q)
        return newId
    }

    override suspend fun insertAllWorkouts(workouts: List<Workout>): List<Long> {
        val newId = workoutDao.insertAllWorkouts(workouts)
        for(i in newId){
            val q = SyncQueue(
                tableName = "workouts",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        return newId
    }

    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
        if(syncQueueDao.getSyncQueueByTableName(workout.workoutId,"workouts") == null){
            val q = SyncQueue(
                tableName = "workouts",
                localId = workout.workoutId,
                globalId = workout.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteWorkout(workout: Workout) {
        if(workout.globalId != null){
            val q = SyncQueue(
                tableName = "workouts",
                localId = workout.workoutId,
                globalId = workout.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(workout.workoutId,"workouts")
            if(existing != null){
                syncQueueDao.deleteSyncQueue(existing)
            }
        }
        workoutDao.deleteWorkout(workout)
    }

    override fun getWorkoutById(id: Int): Flow<Workout> {
        return workoutDao.getWorkoutById(id)
    }

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }

    override fun getWorkoutWithExercisesById(id: Int): Flow<WorkoutWithWorkoutExercises> {
        return workoutDao.getWorkoutWithExercisesById(id)
    }

    override fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithWorkoutExercises>> {
        return workoutDao.getAllWorkoutsWithExercises()
    }

    override suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long {
        return workoutExerciseDao.insertWorkoutExercise(workoutExercise)
    }

    override suspend fun insertAllWorkoutExercises(workoutExercises: List<WorkoutExercise>): List<Long> {
        return workoutExerciseDao.insertAllWorkoutExercises(workoutExercises)
    }

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        workoutExerciseDao.insertWorkoutExercise(workoutExercise)
    }

    override suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise) {
        workoutExerciseDao.deleteWorkoutExercise(workoutExercise)
    }

    override fun getWorkoutExerciseById(id: Int): Flow<WorkoutExercise> {
        return workoutExerciseDao.getWorkoutExerciseById(id)
    }

    override fun getAllWorkoutExercises(): Flow<List<WorkoutExercise>> {
        return workoutExerciseDao.getAllWorkoutExercises()
    }

    override fun getWorkoutExerciseWithSets(id: Int): Flow<WorkoutExerciseWithSets> {
        return workoutExerciseDao.getWorkoutExerciseWithSets(id)
    }

    override fun getAllWorkoutExercisesWithSets(): Flow<List<WorkoutExerciseWithSets>> {
        return workoutExerciseDao.getAllWorkoutExercisesWithSets()
    }
}
