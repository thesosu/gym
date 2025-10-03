package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue

class SyncQueueRepository(context: Context): SyncQueueDao {
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()

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

    override fun getSyncQueueById(id: Int): Flow<SyncQueue> {
        return syncQueueDao.getSyncQueueById(id)
    }

    override fun getAllSyncQueues(): Flow<List<SyncQueue>> {
        return syncQueueDao.getAllSyncQueues()
    }

    override fun getSyncQueueByTableName(id: Int, tn: String): SyncQueue? {
        return syncQueueDao.getSyncQueueByTableName(id,tn)
    }
}