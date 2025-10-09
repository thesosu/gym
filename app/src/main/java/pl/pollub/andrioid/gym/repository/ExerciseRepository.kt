package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.ExerciseDao
import pl.pollub.andrioid.gym.db.dao.ExerciseMuscleGroupDao
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.relationships.ExerciseWithMuscleGroups
import pl.pollub.andrioid.gym.db.relationships.ExerciseWithWorkoutExercise
import pl.pollub.andrioid.gym.db.relationships.ExerciseWithWorkoutTemplates

class ExerciseRepository(context: Context):ExerciseDao, ExerciseMuscleGroupDao {
    private val exerciseDao = AppDb.getInstance(context).exerciseDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    private val exerciseMuscleGroupDao = AppDb.getInstance(context).exerciseMuscleGroupDao()


    override suspend fun insertExercise(exercise: Exercise): Long = withContext(Dispatchers.IO){
        val newId = exerciseDao.insertExercise(exercise)

        val q =SyncQueue(
            tableName = "exercises",
            localId = newId.toInt(),
            userId =
        )
        syncQueueDao.insertSyncQueue(q)
        newId
    }

    override suspend fun insertExercises(exercises: List<Exercise>): List<Long> = withContext(Dispatchers.IO){
        val newId = exerciseDao.insertExercises(exercises)
        for(i in newId){
            val q = SyncQueue(
                tableName = "exercises",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        newId
    }

    override suspend fun updateExercise(exercise: Exercise) = withContext(Dispatchers.IO){
        exerciseDao.updateExercise(exercise)
        if(syncQueueDao.getSyncQueueByTableName(exercise.exerciseId,"exercises") == null){
            val q = SyncQueue(
                tableName = "exercises",
                localId = exercise.exerciseId,
                globalId = exercise.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteExercise(exercise: Exercise) = withContext(Dispatchers.IO){
        if(exercise.globalId != null){
            val q = SyncQueue(
                tableName = "exercises",
                localId = exercise.exerciseId,
                globalId = exercise.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(exercise.exerciseId,"exercises")
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

    override fun getMuscleGroupIdsForExercise(id: Int): List<Int> {
        return exerciseDao.getMuscleGroupIdsForExercise(id)
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

    override suspend fun insertExerciseMuscleGroup(exerciseMuscleGroup: ExerciseMuscleGroup) {
        exerciseMuscleGroupDao.insertExerciseMuscleGroup(exerciseMuscleGroup)
    }

    override suspend fun insertAllExercisesMuscleGroups(exerciseMuscleGroups: List<ExerciseMuscleGroup>) {
        exerciseMuscleGroupDao.insertAllExercisesMuscleGroups(exerciseMuscleGroups)
    }
}
