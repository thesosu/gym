package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.ExerciseDao
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.relationships.ExerciseWithMuscleGroups
import pl.pollub.andrioid.gym.db.relationships.ExerciseWithWorkoutExercise
import pl.pollub.andrioid.gym.db.relationships.ExerciseWithWorkoutTemplates

class ExerciseRepository(context: Context):ExerciseDao {
    private val exerciseDao = AppDb.getInstance(context).exerciseDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    override suspend fun insertExercise(exercise: Exercise): Long {
        val newId = exerciseDao.insertExercise(exercise)

        val q =SyncQueue(
            tableName = "exercises",
            localId = newId.toInt()
        )
        syncQueueDao.insertSyncQueue(q)
        return newId
    }

    override suspend fun insertExercises(exercises: List<Exercise>): List<Long> {
        val newId = exerciseDao.insertExercises(exercises)
        for(i in newId){
            val q = SyncQueue(
                tableName = "exercises",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        return newId
    }

    override suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(exercise)
        if(syncQueueDao.getSyncQueueByTableName(exercise.exerciseId,"exercises") == null){
            val q = SyncQueue(
                tableName = "exercises",
                localId = exercise.exerciseId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        if(exercise.globalId != null){
            val q = SyncQueue(
                tableName = "exercise",
                localId = exercise.exerciseId,
                globalId = exercise.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(exercise.exerciseId,"exercise")
            if(existing != null){
                syncQueueDao.deleteSyncQueue(existing)
            }
        }
        exerciseDao.deleteExercise(exercise)
    }

    override fun getExerciseById(id: Int): Flow<Exercise> {
        return exerciseDao.getExerciseById(id)
    }

    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }

    override fun getExerciseWithMuscleGroups(id: Int): Flow<ExerciseWithMuscleGroups> {
        return exerciseDao.getExerciseWithMuscleGroups(id)
    }

    override fun getExerciseWithWorkoutExercises(id: Int): Flow<ExerciseWithWorkoutExercise> {
        return exerciseDao.getExerciseWithWorkoutExercises(id)
    }

    override fun getExerciseWithWorkoutTemplates(id: Int): Flow<ExerciseWithWorkoutTemplates> {
        return exerciseDao.getExerciseWithWorkoutTemplates(id)
    }

    override fun getAllExercisesWithMuscleGroups(): Flow<List<ExerciseWithMuscleGroups>> {
        return exerciseDao.getAllExercisesWithMuscleGroups()
    }

    override fun getAllExercisesWithWorkoutExercises(): Flow<List<ExerciseWithWorkoutExercise>> {
        return exerciseDao.getAllExercisesWithWorkoutExercises()
    }

    override fun getAllExercisesWithWorkoutTemplates(): Flow<List<ExerciseWithWorkoutTemplates>> {
        return exerciseDao.getAllExercisesWithWorkoutTemplates()
    }
}
