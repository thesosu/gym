package pl.pollub.andrioid.gym.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement

@Dao
interface BodyMeasurementDao {

    @Query("SELECT * FROM body_measurements")
    fun getAllBodyMeasurements(): Flow<List<BodyMeasurement>>

    @Query("SELECT * FROM body_measurements WHERE body_measurement_id = :id")
    fun getBodyMeasurementById(id: Int): Flow<BodyMeasurement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurement>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMeasurement(bodyMeasurements: BodyMeasurement): Long

    @Update
    suspend fun updateBodyMeasurement(bodyMeasurement: BodyMeasurement)

    @Delete
    suspend fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurement)

    @Query("DELETE FROM body_measurements WHERE global_id = :id")
    suspend fun deleteBodyMeasurementByGlobalId(id: Int)
}