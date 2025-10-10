package pl.pollub.andrioid.gym.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.network.ApiClient
import pl.pollub.andrioid.gym.network.dto.reguest.BodyMeasurementRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ExerciseRequest
import pl.pollub.andrioid.gym.network.dto.reguest.SetRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutExerciseRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutTemplatesRequest
import java.time.Instant

class SyncQueueRepository(context: Context): SyncQueueDao {
    private val db = AppDb.getInstance(context)
    private val syncQueueDao = db.syncQueueDao()
    private val userDao = db.userDao()
    private val exerciseDao = db.exerciseDao()
    private val bodyMeasurementDao = db.bodyMeasurementDao()
    private val workoutDao = db.workoutDao()
    private val workoutTemplateDao = db.workoutTemplateDao()
    private val api = ApiClient.create(context)

    suspend fun sync()= withContext(Dispatchers.IO){
        try {
            val userId = userDao.getLoggedInUserId()
            if (userId != null) {
                assignSyncQueueToUser(userId)
                val lastSyncResponse = api.getUserLastSync().lastSync
                val lastSyncResponseInstant = Instant.parse(lastSyncResponse)
                val lastSync = userDao.getLastSync()?.let(Instant::parse)

                if (lastSync == null || lastSyncResponseInstant.isAfter(lastSync)) {
                    downloadFromServer(lastSync.toString(),lastSyncResponse)
                }
                uploadToServer(lastSyncResponse)

            }
        }catch (e: Exception) {
            Log.e("SyncRepository", "Sync failed: ${e.message}")
        }

    }
    private suspend fun assignSyncQueueToUser(userId: Int) {
        val syncQueue = syncQueueDao.getSyncQueuesByUserId(null)
        for(q in syncQueue){
            q.userId =userId
            syncQueueDao.updateSyncQueue(q)
        }

    }
    suspend fun downloadFromServer(startDate: String, endDate: String)= withContext(Dispatchers.IO){

        downloadExercises(startDate, endDate)
        downloadWorkouts(startDate, endDate)
        downloadWorkoutTemplates(startDate, endDate)
        downloadBodyMeasurements(startDate, endDate)

        userDao.updateLastSync(endDate)

    }

    suspend fun uploadToServer(initialLastSync: String)= withContext(Dispatchers.IO){
        val userId = userDao.getLoggedInUserId()
        val syncQueue = syncQueueDao.getSyncQueuesByUserId(userId!!)
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
                        lastSync = currentLastSync
                    )
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
                        id = q.globalId!!,
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
                        id = q.globalId!!,
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
        val bodyMeasurement = q.localId?.let { bodyMeasurementDao.getBodyMeasurementById(it).firstOrNull() }

        val request = bodyMeasurement?.let {
            BodyMeasurementRequest(
                weight = it.weight,
                arm = it.arm,
                forearm = it.forearm,
                chest = it.chest,
                waist = it.waist,
                hips = it.hips,
                thigh = it.thigh,
                calf = it.calf,
                date = it.date
            )
        }

        var currentLastSync = lastSync
        try {
            when {
                // Add
                q.globalId == null && bodyMeasurement != null -> {
                    val response = api.addBodyMeasurement(
                        request = request!!,
                        lastSync = currentLastSync
                    )
                    bodyMeasurement.globalId = response.id
                    bodyMeasurementDao.updateBodyMeasurement(bodyMeasurement)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Add complete")
                }

                // Update
                q.globalId != null && bodyMeasurement != null -> {
                    val response = api.updateBodyMeasurement(
                        id = q.globalId!!,
                        request = request!!,
                        lastSync = currentLastSync,
                    )
                    bodyMeasurementDao.updateBodyMeasurement(bodyMeasurement)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Update complete")
                }

                // Delete
                q.globalId != null && bodyMeasurement == null -> {
                    val response = api.deleteBodyMeasurement(
                        id = q.globalId!!,
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
            Log.e("SyncRepository", "BodyMeasurement sync error: ${e.message}")
        }

        return currentLastSync

    }
    private suspend fun syncWorkout(q: SyncQueue, lastSync: String): String{

        val workout = q.localId?.let { workoutDao.getWorkoutById(it).firstOrNull() }
        val exercisesWithSets = q.localId?.let { workoutDao.getExercisesWithSetsForWorkout(it) } ?: emptyList()

        if (exercisesWithSets.size > 50) {
            Log.e("SyncRepository", "Cannot sync: Maximum 50 exercises per workout")
            return lastSync
        }

        exercisesWithSets.forEach { ex ->
            if (ex.sets.size > 30) {
                Log.e("SyncRepository", "Cannot sync: Maximum 30 sets per exercise (exerciseId=${ex.workoutExercise.exerciseId})")
                return lastSync
            }
        }

        val exercisesRequest = exercisesWithSets.map { we ->
            WorkoutExerciseRequest(
                id = we.workoutExercise.workoutExerciseId,
                sets = we.sets.map { s ->
                    SetRequest(
                        reps = s.reps,
                        weight = s.weight
                    )
                }
            )
        }

        val request = workout?.let {
            WorkoutRequest(
                duration = it.duration,
                template = it.workoutTemplateId,
                date = it.date,
                exercises = exercisesRequest
            )
        }

        var currentLastSync = lastSync
        try {
            when {
                // Add
                q.globalId == null && workout != null -> {
                    val response = api.addWorkout(
                        request = request!!,
                        lastSync = currentLastSync
                    )
                    workout.globalId = response.id
                    workoutDao.updateWorkout(workout)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Add complete")
                }

                // Update
                q.globalId != null && workout != null -> {
                    val response = api.updateWorkout(
                        id = q.globalId!!,
                        request = request!!,
                        lastSync = currentLastSync,
                    )
                    workoutDao.updateWorkout(workout)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Update complete")
                }

                // Delete
                q.globalId != null && workout == null -> {
                    val response = api.deleteWorkout(
                        id = q.globalId!!,
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
            Log.e("SyncRepository", "Workout sync error: ${e.message}")
        }

        return currentLastSync
    }
    private suspend fun syncWorkoutTemplate(q: SyncQueue, lastSync: String): String{
        val template = q.localId?.let { workoutTemplateDao.getWorkoutTemplateById(it).firstOrNull() }
        val exerciseIds = q.localId?.let { workoutTemplateDao.getExerciseIdsForTemplate(it) } ?: emptyList()
        if (exerciseIds.size > 50) {
            Log.e("SyncRepository", "Cannot sync: Maximum 50 exercises per template")
            return lastSync
        }

        val request = template?.let {
            WorkoutTemplatesRequest(
                name = it.name,
                exercises = exerciseIds
            )
        }

        var currentLastSync = lastSync
        try {
            when {
                // Add
                q.globalId == null && template != null -> {
                    val response = api.addWorkoutTemplate(
                        request = request!!,
                        lastSync = currentLastSync
                    )
                    template.globalId = response.id
                    workoutTemplateDao.updateWorkoutTemplate(template)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Add complete")
                }

                // Update
                q.globalId != null && template != null -> {
                    val response = api.updateWorkoutTemplate(
                        id = q.globalId!!,
                        request = request!!,
                        lastSync = currentLastSync,
                    )
                    workoutTemplateDao.updateWorkoutTemplate(template)
                    syncQueueDao.deleteSyncQueue(q)
                    userDao.updateLastSync(response.lastSync)
                    currentLastSync = response.lastSync
                    Log.d("SyncRepository", "Update complete")
                }

                // Delete
                q.globalId != null && template == null -> {
                    val response = api.deleteWorkoutTemplate(
                        id = q.globalId!!,
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
            Log.e("SyncRepository", "Template sync error: ${e.message}")
        }

        return currentLastSync

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


    override fun getSyncQueueById(id: Int?): SyncQueue {
        return syncQueueDao.getSyncQueueById(id)
    }

    override fun getSyncQueuesByUserId(id: Int?): List<SyncQueue> {
        return syncQueueDao.getSyncQueuesByUserId(id)
    }

    override fun getSyncQueueByTableName(id: Int?, tn: String): SyncQueue? {
        return syncQueueDao.getSyncQueueByTableName(id,tn)
    }
}