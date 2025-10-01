package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.BodyMeasurementDao
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement
import pl.pollub.andrioid.gym.db.entity.SyncQueue

class BodyMeasurementRepository(context: Context):BodyMeasurementDao {
    private val bodyMeasurementDao = AppDb.getInstance(context).bodyMeasurementDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()

    override fun getAllBodyMeasurements(): Flow<List<BodyMeasurement>> {
        return bodyMeasurementDao.getAllBodyMeasurements()
    }

    override fun getBodyMeasurementById(id: Int): Flow<BodyMeasurement> {
        return bodyMeasurementDao.getBodyMeasurementById(id)
    }

    override suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement>): List<Long> {
        val newId = bodyMeasurementDao.insertBodyMeasurements(bodyMeasurements)
        for(i in newId){
            val q = SyncQueue(
                tableName = "body_measurements",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        return newId    }

    override suspend fun insertBodyMeasurement(bodyMeasurements: BodyMeasurement): Long {
        val newId = bodyMeasurementDao.insertBodyMeasurement(bodyMeasurements)
        val q =SyncQueue(
            tableName = "body_measurements",
            localId = newId.toInt()
        )
        syncQueueDao.insertSyncQueue(q)
        return newId
    }

    override suspend fun updateBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        bodyMeasurementDao.updateBodyMeasurement(bodyMeasurement)
        if(syncQueueDao.getSyncQueueByTableName(bodyMeasurement.bodyMeasurementId,"body_measurements") == null){
            val q = SyncQueue(
                tableName = "body_measurements",
                localId = bodyMeasurement.bodyMeasurementId,
                globalId = bodyMeasurement.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        if(bodyMeasurement.globalId != null){
            val q = SyncQueue(
                tableName = "body_measurements",
                localId = bodyMeasurement.bodyMeasurementId,
                globalId = bodyMeasurement.globalId
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
}