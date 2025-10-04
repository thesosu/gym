package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.network.ApiClient
import pl.pollub.andrioid.gym.network.Dto.ExerciseRequest

class SyncQueueRepository(context: Context): SyncQueueDao {
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    private val exerciseDao = AppDb.getInstance(context).exerciseDao()

    private val api = ApiClient.api

    suspend fun addToServer()= withContext(Dispatchers.IO){
        val syncQueue = syncQueueDao.getAllSyncQueues()
        for (q in syncQueue){
            if(q.tableName == "exercises"){
                val exercise = exerciseDao.getExerciseById(q.localId).first()
                val request = ExerciseRequest(
                    userId = exercise.userId,
                    name = exercise.name,
                    description = exercise.description
                )

                try {
                    val response = api.insertExercise(request)
                    exercise.globalId = response.id
                    exerciseDao.updateExercise(exercise)
                    syncQueueDao.deleteSyncQueue(q)

                }catch (e:Exception){
                    println("blad: "+e.message)
                }
            }
        }
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

    override fun getAllSyncQueues(): List<SyncQueue> {
        return syncQueueDao.getAllSyncQueues()
    }

    override fun getSyncQueueByTableName(id: Int, tn: String): SyncQueue? {
        return syncQueueDao.getSyncQueueByTableName(id,tn)
    }
}