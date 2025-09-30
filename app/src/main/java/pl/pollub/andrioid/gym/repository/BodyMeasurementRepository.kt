package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.dao.BodyMeasurementDao
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement

class BodyMeasurementRepository(context: Context):BodyMeasurementDao {
    override fun getAllBodyMeasurements(): Flow<List<BodyMeasurement>> {
        TODO("Not yet implemented")
    }

    override fun getBodyMeasurementById(id: Int): Flow<BodyMeasurement> {
        TODO("Not yet implemented")
    }

    override suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement>) {
        TODO("Not yet implemented")
    }

    override suspend fun insertBodyMeasurement(bodyMeasurements: BodyMeasurement) {
        TODO("Not yet implemented")
    }

    override suspend fun updateBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        TODO("Not yet implemented")
    }
}