package pl.pollub.andrioid.gym.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.Set
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplateExercise
import pl.pollub.andrioid.gym.network.ApiClient
import pl.pollub.andrioid.gym.network.dto.reguest.BodyMeasurementRequest
import pl.pollub.andrioid.gym.network.dto.reguest.ExerciseRequest
import pl.pollub.andrioid.gym.network.dto.reguest.SetRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutExerciseRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutRequest
import pl.pollub.andrioid.gym.network.dto.reguest.WorkoutTemplatesRequest
import retrofit2.HttpException
import java.time.Instant

class SyncQueueRepository(context: Context) {
    private val db = AppDb.getInstance(context)
    private val syncQueueDao = db.syncQueueDao()
    private val userDao = db.userDao()
    private val exerciseDao = db.exerciseDao()
    private val exerciseMuscleGroupDao = db.exerciseMuscleGroupDao()
    private val bodyMeasurementDao = db.bodyMeasurementDao()
    private val workoutDao = db.workoutDao()
    private val workoutExerciseDao = db.workoutExerciseDao()
    private val workoutTemplateDao = db.workoutTemplateDao()
    private val workoutTemplateExerciseDao = db.workoutTemplateExerciseDao()

    private val setDao = db.setDao()
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
                    try{downloadFromServer(lastSync?.toString(),lastSyncResponse)}
                    catch (e: HttpException) {
                        if (e.code() != 404) throw e
                        Log.w("SyncRepository", "No data to download.")
                    }

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
    suspend fun downloadFromServer(startDate: String?, endDate: String)= withContext(Dispatchers.IO){

        downloadExercises(startDate, endDate)
        downloadWorkouts(startDate, endDate)
        downloadWorkoutTemplates(startDate, endDate)
        downloadBodyMeasurements(startDate, endDate)

        userDao.updateLastSync(endDate)

    }
    private suspend fun downloadExercises(startDate: String?, endDate: String) {
        var offset = 0
        val limit = 20
        var hasMore: Boolean
        val userId = userDao.getLoggedInUserId()

        do {
            val response = api.getExercises(offset, limit, startDate, endDate)
            hasMore = response.hasMore

            for (exercise in response.data) {
                if (exercise.deleted) {
                    exerciseDao.deleteExerciseByGlobalId(exercise.id)
                } else {
                    val exerciseEntity = Exercise(
                        globalId = exercise.id,
                        name = exercise.name,
                        description = exercise.description,
                        userId = userId
                    )
                    val localExerciseId = exerciseDao.insertExercise(exerciseEntity)

                    exercise.muscleGroups?.forEach { mgId ->
                        exerciseMuscleGroupDao.insertExerciseMuscleGroup(
                            ExerciseMuscleGroup(
                                muscleGroupId = mgId,
                                exerciseId = localExerciseId.toInt(),
                            )
                        )
                    }
                }
            }
            offset += limit
        } while (hasMore)
    }

    private suspend fun downloadWorkouts(startDate: String?, endDate: String) {
        var offset = 0
        val limit = 5
        var hasMore: Boolean
        val userId = userDao.getLoggedInUserId()

        do {
            val response = api.getUserWorkouts(offset, limit, startDate, endDate)
            hasMore = response.hasMore

            for (workout in response.data) {
                if (workout.deleted) {
                    workoutDao.deleteWorkoutByGlobalId(workout.id)
                } else {
                    val workoutEntity = Workout(
                        globalId = workout.id,
                        duration = workout.duration,
                        date = workout.date,
                        userId = userId,
                        workoutTemplateId = workout.template,
                    )
                    val localWorkoutId = workoutDao.insertWorkout(workoutEntity)

                    // ćwiczenia wewnątrz treningu
                    for (ex in workout.exercises) {
                        val localWorkoutExerciseId = workoutExerciseDao.insertWorkoutExercise(
                            WorkoutExercise(
                                position = ex.position,
                                exerciseId = ex.id,
                                workoutId = localWorkoutId.toInt()

                            )
                        )

                        ex.sets.forEachIndexed { index, set ->
                        setDao.insertSet(
                            Set(
                                workoutExerciseId = localWorkoutExerciseId.toInt(),
                                reps = set.reps,
                                weight = set.weight,
                                position = index
                            )
                        )
                        }

                    }
                }
            }
            offset += limit
        } while (hasMore)
    }

    private suspend fun downloadBodyMeasurements(startDate: String?, endDate: String) {
        var offset = 0
        val limit = 20
        var hasMore: Boolean
        val userId = userDao.getLoggedInUserId()

        do {
            val response = api.getUserMeasurements(offset, limit, startDate, endDate)
            hasMore = response.hasMore

            for (bm in response.data) {
                if (bm.deleted) {
                    bodyMeasurementDao.deleteBodyMeasurementByGlobalId(bm.id)
                } else {
                    val entity = BodyMeasurement(
                        globalId = bm.id,
                        weight = bm.weight,
                        arm = bm.arm,
                        forearm = bm.forearm,
                        chest = bm.chest,
                        waist = bm.waist,
                        hips = bm.hips,
                        thigh = bm.thigh,
                        calf = bm.calf,
                        date = bm.date,
                        userId = userId
                    )
                    bodyMeasurementDao.insertBodyMeasurement(entity)
                }
            }
            offset += limit
        } while (hasMore)
    }

    private suspend fun downloadWorkoutTemplates(startDate: String?, endDate: String) {
        var offset = 0
        val limit = 20
        var hasMore: Boolean
        val userId = userDao.getLoggedInUserId()
        do {
            val response = api.getUserWorkoutTemplates(offset, limit, startDate, endDate)
            hasMore = response.hasMore

            for (template in response.data) {
                if (template.deleted) {
                    workoutTemplateDao.deleteWorkoutTemplateByGlobalId(template.id)
                } else {
                    val workoutTemplate = WorkoutTemplate(
                        globalId = template.id,
                        name = template.name,
                        userId = userId
                    )
                    val localTemplateId = workoutTemplateDao.insertWorkoutTemplate(workoutTemplate)
                    template.exercises.forEachIndexed { index, eId ->
                        workoutTemplateExerciseDao.insertWorkoutTemplateExercise(
                            WorkoutTemplateExercise(
                                workoutTemplateId = localTemplateId.toInt(),
                                exerciseId = eId,
                                position = index
                            )
                        )
                    }
                }
            }
            offset += limit
        } while (hasMore)
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

    suspend fun insertSyncQueue(syncQueue: SyncQueue): Long = withContext(Dispatchers.IO){
        syncQueueDao.insertSyncQueue(syncQueue)
    }

    suspend fun insertSyncQueues(syncQueues: List<SyncQueue>): List<Long> = withContext(Dispatchers.IO){
        syncQueueDao.insertSyncQueues(syncQueues)
    }

    suspend fun updateSyncQueue(syncQueue: SyncQueue) = withContext(Dispatchers.IO){
        syncQueueDao.updateSyncQueue(syncQueue)
    }

    suspend fun deleteSyncQueue(syncQueue: SyncQueue) = withContext(Dispatchers.IO){
        syncQueueDao.deleteSyncQueue(syncQueue)
    }


    fun getSyncQueueById(id: Int?): SyncQueue {
        return syncQueueDao.getSyncQueueById(id)
    }

    fun getSyncQueuesByUserId(id: Int?): List<SyncQueue> {
        return syncQueueDao.getSyncQueuesByUserId(id)
    }

    fun getSyncQueueByTableName(id: Int?, tn: String): SyncQueue? {
        return syncQueueDao.getSyncQueueByTableName(id,tn)
    }
}