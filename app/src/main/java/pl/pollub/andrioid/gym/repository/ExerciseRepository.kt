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

class ExerciseRepository(context: Context) {

    private val db = AppDb.getInstance(context)
    private val exerciseDao = db.exerciseDao()
    private val syncQueueDao = db.syncQueueDao()
    private val exerciseMuscleGroupDao = db.exerciseMuscleGroupDao()
    private val userDao = db.userDao()


    suspend fun insertExercise(exercise: Exercise,muscleGroupIds: List<Int>): Long = withContext(Dispatchers.IO){
        val userId = userDao.getLoggedInUserId()
        val newId = exerciseDao.insertExercise(exercise.copy(userId = userId))

        val q =SyncQueue(
            tableName = "exercises",
            localId = newId.toInt(),
            userId = userId
        )
        syncQueueDao.insertSyncQueue(q)
        muscleGroupIds.forEach { mgId ->
            exerciseMuscleGroupDao.insertExerciseMuscleGroup(ExerciseMuscleGroup(muscleGroupId = mgId, exerciseId = newId.toInt()))
        }
        newId
    }
    suspend fun insertExercisesWithGroups(exerciseToMuscleMap: Map<Exercise, List<Int>>): List<Long> = withContext(Dispatchers.IO) {
        val userId = userDao.getLoggedInUserId()
        val insertedIds = mutableListOf<Long>()

        exerciseToMuscleMap.forEach { (exercise, muscleIds) ->
            val newId = exerciseDao.insertExercise(exercise.copy(userId = userId))
            insertedIds.add(newId)

            val q = SyncQueue(
                tableName = "exercises",
                localId = newId.toInt(),
                userId = userId
            )
            syncQueueDao.insertSyncQueue(q)

            muscleIds.forEach { mgId ->
                exerciseMuscleGroupDao.insertExerciseMuscleGroup(
                    ExerciseMuscleGroup(
                        exerciseId = newId.toInt(),
                        muscleGroupId = mgId
                    )
                )
            }
        }

        insertedIds
    }


    suspend fun updateExercise(exercise: Exercise) = withContext(Dispatchers.IO){
        val userId = userDao.getLoggedInUserId()
        exerciseDao.updateExercise(exercise.copy(userId = userId))

        if(syncQueueDao.getSyncQueueByTableName(exercise.exerciseId,"exercises") == null){
            val q = SyncQueue(
                tableName = "exercises",
                localId = exercise.exerciseId,
                globalId = exercise.globalId,
                userId = userId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    suspend fun deleteExercise(exercise: Exercise) = withContext(Dispatchers.IO){
        val userId = userDao.getLoggedInUserId()

        if(exercise.globalId != null){
            val q = SyncQueue(
                tableName = "exercises",
                localId = exercise.exerciseId,
                globalId = exercise.globalId,
                userId = userId
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

    suspend fun deleteExerciseByGlobalId(id: Int) {
        exerciseDao.deleteExerciseByGlobalId(id)
    }

    fun getExerciseById(id: Int): Flow<Exercise> {
        return exerciseDao.getExerciseById(id)
    }

    fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }

    fun getMuscleGroupIdsForExercise(id: Int): List<Int> {
        return exerciseDao.getMuscleGroupIdsForExercise(id)
    }

    fun getExerciseWithMuscleGroups(id: Int): Flow<ExerciseWithMuscleGroups> {
        return exerciseDao.getExerciseWithMuscleGroups(id)
    }

    fun getExerciseWithWorkoutExercises(id: Int): Flow<ExerciseWithWorkoutExercise> {
        return exerciseDao.getExerciseWithWorkoutExercises(id)
    }

    fun getExerciseWithWorkoutTemplates(id: Int): Flow<ExerciseWithWorkoutTemplates> {
        return exerciseDao.getExerciseWithWorkoutTemplates(id)
    }

    fun getAllExercisesWithMuscleGroups(): Flow<List<ExerciseWithMuscleGroups>> {
        return exerciseDao.getAllExercisesWithMuscleGroups()
    }

    fun getAllExercisesWithWorkoutExercises(): Flow<List<ExerciseWithWorkoutExercise>> {
        return exerciseDao.getAllExercisesWithWorkoutExercises()
    }

    fun getAllExercisesWithWorkoutTemplates(): Flow<List<ExerciseWithWorkoutTemplates>> {
        return exerciseDao.getAllExercisesWithWorkoutTemplates()
    }

    suspend fun insertExerciseMuscleGroup(exerciseMuscleGroup: ExerciseMuscleGroup) {
        exerciseMuscleGroupDao.insertExerciseMuscleGroup(exerciseMuscleGroup)
    }

    suspend fun insertAllExercisesMuscleGroups(exerciseMuscleGroups: List<ExerciseMuscleGroup>) {
        exerciseMuscleGroupDao.insertAllExercisesMuscleGroups(exerciseMuscleGroups)
    }
}
