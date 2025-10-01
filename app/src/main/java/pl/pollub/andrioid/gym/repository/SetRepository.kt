package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SetDao
import pl.pollub.andrioid.gym.db.entity.Set
import pl.pollub.andrioid.gym.db.entity.SyncQueue

class SetRepository(context: Context): SetDao {
    private val setDao = AppDb.getInstance(context).setDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    override suspend fun insertSet(set: Set): Long {
        val newId = setDao.insertSet(set)

        val q = SyncQueue(
            tableName = "sets",
            localId = newId.toInt()
        )
        syncQueueDao.insertSyncQueue(q)
        return newId
    }

    override suspend fun insertSets(sets: List<Set>): List<Long> {
        val newId = setDao.insertSets(sets)
        for(i in newId){
            val q = SyncQueue(
                tableName = "sets",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        return newId
    }

    override suspend fun updateSet(set: Set) {
        setDao.updateSet(set)
        if(syncQueueDao.getSyncQueueByTableName(set.setId,"sets") == null){
            val q = SyncQueue(
                tableName = "sets",
                localId = set.setId,
                globalId = set.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteSet(set: Set) {
        if(set.globalId != null){
            val q = SyncQueue(
                tableName = "sets",
                localId = set.setId,
                globalId = set.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(set.setId,"sets")
            if(existing != null){
                syncQueueDao.deleteSyncQueue(existing)
            }
        }
        setDao.deleteSet(set)
    }

    override fun getSetById(id: Int): Flow<Set> {
        return setDao.getSetById(id)
    }

    override fun getAllSets(): Flow<List<Set>> {
        return setDao.getAllSets()
    }
}