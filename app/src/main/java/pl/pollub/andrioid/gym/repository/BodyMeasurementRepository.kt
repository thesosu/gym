package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.BodyMeasurementDao
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement
import pl.pollub.andrioid.gym.db.entity.SyncQueue

class BodyMeasurementRepository(context: Context) {

    private val db = AppDb.getInstance(context)
    private val bodyMeasurementDao = db.bodyMeasurementDao()
    private val syncQueueDao = db.syncQueueDao()
    private val userDao = db.userDao()

    fun getAllBodyMeasurements(): Flow<List<BodyMeasurement>> {
        return bodyMeasurementDao.getAllBodyMeasurements()
    }

    fun getBodyMeasurementById(id: Int): Flow<BodyMeasurement> {
        return bodyMeasurementDao.getBodyMeasurementById(id)
    }

    suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement>): List<Long> = withContext(
        Dispatchers.IO){
        val newId = bodyMeasurementDao.insertBodyMeasurements(bodyMeasurements)
        val userId = userDao.getLoggedInUserId()

        for(i in newId){
            val q = SyncQueue(
                tableName = "body_measurements",
                localId = i.toInt(),
                userId = userId
            )
            syncQueueDao.insertSyncQueue(q)
        }
        newId
    }

    suspend fun insertBodyMeasurement(bodyMeasurements: BodyMeasurement): Long = withContext(Dispatchers.IO){
        val newId = bodyMeasurementDao.insertBodyMeasurement(bodyMeasurements)
        val userId = userDao.getLoggedInUserId()

        val q =SyncQueue(
            tableName = "body_measurements",
            localId = newId.toInt(),
            userId = userId
        )
        syncQueueDao.insertSyncQueue(q)
        newId
    }

    suspend fun updateBodyMeasurement(bodyMeasurement: BodyMeasurement) = withContext(Dispatchers.IO){
        bodyMeasurementDao.updateBodyMeasurement(bodyMeasurement)
        val userId = userDao.getLoggedInUserId()

        if(syncQueueDao.getSyncQueueByTableName(bodyMeasurement.bodyMeasurementId,"body_measurements") == null){
            val q = SyncQueue(
                tableName = "body_measurements",
                localId = bodyMeasurement.bodyMeasurementId,
                globalId = bodyMeasurement.globalId,
                userId = userId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    suspend fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurement) = withContext(Dispatchers.IO){
        if(bodyMeasurement.globalId != null){
            val userId = userDao.getLoggedInUserId()

            val q = SyncQueue(
                tableName = "body_measurements",
                localId = bodyMeasurement.bodyMeasurementId,
                globalId = bodyMeasurement.globalId,
                userId = userId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(bodyMeasurement.bodyMeasurementId,"body_measurements")
            if(existing != null){
                syncQueueDao.deleteSyncQueue(existing)
            }
        }
        bodyMeasurementDao.deleteBodyMeasurement(bodyMeasurement)
    }

    suspend fun deleteBodyMeasurementByGlobalId(id: Int) {
        bodyMeasurementDao.deleteBodyMeasurementByGlobalId(id)
    }
}