package pl.pollub.andrioid.gym.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.network.ApiClient
import pl.pollub.andrioid.gym.network.dto.reguest.ExerciseRequest
import java.time.Instant

class SyncQueueRepository(context: Context): SyncQueueDao {
    private val db = AppDb.getInstance(context)
    private val syncQueueDao = db.syncQueueDao()
    private val userDao = db.userDao()
    private val exerciseDao = db.exerciseDao()
    private val bodyMeasurementDao = db.bodyMeasurementDao()
    private val muscleGroupDao = db.muscleGroupDao()
    private val exerciseMuscleGroupDao = db.exerciseMuscleGroupDao()
    private val api = ApiClient.create(context)

    suspend fun sync()= withContext(Dispatchers.IO){
        try {
            if (userDao.isAnyUserLoggedIn()) {
                val lastSyncResponse = api.getUserLastSync().lastSync
                val lastSyncResponseInstant = Instant.parse(lastSyncResponse)
                val lastSync = userDao.getLastSync()?.let(Instant::parse)

                if (lastSync == null || lastSyncResponseInstant.isAfter(lastSync)) {
                    downloadFromServer()
                }
                uploadToServer(lastSyncResponse)

            }
        }catch (e: Exception) {
            Log.e("SyncRepository", "Sync failed: ${e.message}")
        }

    }
    suspend fun downloadFromServer()= withContext(Dispatchers.IO){

    }

    suspend fun uploadToServer(initialLastSync: String)= withContext(Dispatchers.IO){
        val userId = userDao.getLoggedInUserId()
        val syncQueue = syncQueueDao.getSyncQueuesByUserId(userId)
        var currentLastSync = initialLastSync

        for (q in syncQueue){
            try{
                when(q.tableName){
                    "exercises" -> currentLastSync = syncExercise(q, currentLastSync)
                    "body_measurements" -> currentLastSync = syncBodyMeasurement(q, currentLastSync)
                    "workouts" -> currentLastSync = syncWorkout(q, currentLastSync)
                    "workout_templates" -> currentLastSync = syncWorkoutTemplate(q, currentLastSync)
                    else -> Log.w("SyncRepository", "Unknown table: ${q.tableName}")
                }
            }catch (e:Exception){
                Log.e("SyncRepository", " Sync failed for ${q.tableName}: ${e.message}")
            }

        }
    }
    private suspend fun syncExercise(q: SyncQueue,lastSync: String): String{

        val exercise = q.localId?.let { exerciseDao.getExerciseById(it).firstOrNull() }
        val muscleGroupIds = q.localId?.let { exerciseDao.getMuscleGroupIdsForExercise(it) } ?: emptyList()
        val request = exercise?.let {
            ExerciseRequest(
                name = it.name,
                description = it.description,
                muscleGroups = muscleGroupIds
            )
        }

        var currentLastSync = lastSync
        try {
            when {
                // Add
                q.globalId == null && exercise != null -> {
                    val response = api.addExercise(
                        request = request!!,
                        lastSync = currentLastSync)
                    exercise.globalId = response.id
                    exerciseDao.updateExercise(exercise)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Add complete")
                }

                // Update
                q.globalId != null && exercise != null -> {
                    val response = api.updateExercise(
                        id = q.globalId,
                        request = request!!,
                        lastSync = currentLastSync,
                    )
                    exerciseDao.updateExercise(exercise)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Update complete")
                }

                // Delete
                q.globalId != null && exercise == null -> {
                    val response = api.deleteExercise(
                        id = q.globalId,
                        lastSync = currentLastSync
                    )
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Delete complete")
                }

                else -> {
                    Log.w("SyncRepository", "Nothing to sync for exercise queue: $q")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncRepository", "Exercise sync error: ${e.message}")
        }

        return currentLastSync
    }


    private suspend fun syncBodyMeasurement(q: SyncQueue, lastSync: String): String{

    }
    private suspend fun syncWorkout(q: SyncQueue, lastSync: String): String{

    }
    private suspend fun syncWorkoutTemplate(q: SyncQueue, lastSync: String): String{

    }

    override suspend fun insertSyncQueue(syncQueue: SyncQueue): Long = withContext(Dispatchers.IO){
        syncQueueDao.insertSyncQueue(syncQueue)
    }

    override suspend fun insertSyncQueues(syncQueues: List<SyncQueue>): List<Long> = withContext(Dispatchers.IO){
        syncQueueDao.insertSyncQueues(syncQueues)
    }

    override suspend fun updateSyncQueue(syncQueue: SyncQueue) = withContext(Dispatchers.IO){
        syncQueueDao.updateSyncQueue(syncQueue)
    }

    override suspend fun deleteSyncQueue(syncQueue: SyncQueue) = withContext(Dispatchers.IO){
        syncQueueDao.deleteSyncQueue(syncQueue)
    }

    override fun getSyncQueueById(id: Int): SyncQueue {
        return syncQueueDao.getSyncQueueById(id)
    }

    override fun getSyncQueuesByUserId(id: Int): List<SyncQueue> {
        return syncQueueDao.getSyncQueuesByUserId(id)
    }

    override fun getSyncQueueByTableName(id: Int, tn: String): SyncQueue? {
        return syncQueueDao.getSyncQueueByTableName(id,tn)
    }
}