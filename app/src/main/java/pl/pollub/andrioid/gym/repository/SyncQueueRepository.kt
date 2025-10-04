package pl.pollub.andrioid.gym.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.network.ApiClient
import pl.pollub.andrioid.gym.network.Dto.ExerciseDto
import pl.pollub.andrioid.gym.network.Dto.MuscleGroupDto

class SyncQueueRepository(context: Context): SyncQueueDao {
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    private val exerciseDao = AppDb.getInstance(context).exerciseDao()
    private val bodyMeasurementDao = AppDb.getInstance(context).bodyMeasurementDao()
    private val muscleGroupDao = AppDb.getInstance(context).muscleGroupDao()
    private val exerciseMuscleGroupDao = AppDb.getInstance(context).exerciseMuscleGroupDao()

    private val api = ApiClient.api

    suspend fun addToServer()= withContext(Dispatchers.IO){
        val syncQueue = syncQueueDao.getAllSyncQueues()
        for (q in syncQueue){
            try{
                when(q.tableName){
                    "exercises" -> syncExercise(q)
                    "muscle_groups" -> syncMuscleGroup(q)
                    "body_measurements" -> syncBodyMeasurement(q)
                    "workouts" -> syncWorkout(q)
                    "workout_templates" -> syncWorkoutTemplate(q)
                    else -> Log.w("SyncRepository", "Unknown table: ${q.tableName}")

                }
            }catch (e:Exception){
                Log.e("SyncRepository", " Sync failed for ${q.tableName}: ${e.message}")
            }

        }
    }
    private suspend fun syncExercise(q: SyncQueue){
        when{
            //Add
            q.globalId == null && q.localId != null ->{

                val exercise = exerciseDao.getExerciseById(q.localId).first()
                val muscleGroupIds = exerciseDao.getMuscleGroupIdsForExercise(q.localId)
                val request = ExerciseDto(
                    id = null,
                    name = exercise.name,
                    description = exercise.description,
                    muscleGroups = muscleGroupIds
                )

                try {
                    val response = api.insertExercise(request)
                    exercise.globalId = response.id
                    exerciseDao.updateExercise(exercise)
                    syncQueueDao.deleteSyncQueue(q)
                    println("blad: "+request)

                }catch (e:Exception){
                    println("blad: "+e.message)
                }


            }
            //Update
            q.globalId != null && q.localId != null ->{

            }
            //Delete
            q.globalId != null && q.localId == null ->{

            }
        }




    }
    private suspend fun syncMuscleGroup(q: SyncQueue){
        when{
            //Add
            q.globalId == null && q.localId != null ->{

                val muscleGroup = muscleGroupDao.getMuscleGroupById(q.localId).first()
                val request = MuscleGroupDto(
                    id = null,
                    name = muscleGroup.name
                )

                try {
                    val response = api.insertMuscleGroup(request)
                    muscleGroup.globalId = response.id
                    muscleGroupDao.updateMuscleGroup(muscleGroup)
                    syncQueueDao.deleteSyncQueue(q)
                    println("ok: "+request)

                }catch (e:Exception){
                    println("blad: "+e.message)
                }


            }
            //Update
            q.globalId != null && q.localId != null ->{

            }
            //Delete
            q.globalId != null && q.localId == null ->{

            }
        }


    }
    private suspend fun syncBodyMeasurement(q: SyncQueue){

    }
    private suspend fun syncWorkout(q: SyncQueue){

    }
    private suspend fun syncWorkoutTemplate(q: SyncQueue){

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