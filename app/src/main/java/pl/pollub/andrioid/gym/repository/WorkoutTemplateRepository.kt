package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.WorkoutTemplateDao
import pl.pollub.andrioid.gym.db.dao.WorkoutTemplateExerciseDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplateExercise
import pl.pollub.andrioid.gym.db.relationships.WorkoutTemplateWithExercises
import pl.pollub.andrioid.gym.db.relationships.WorkoutTemplateWithWorkouts

class WorkoutTemplateRepository(context: Context): WorkoutTemplateDao, WorkoutTemplateExerciseDao {
    private val workoutTemplateDao = AppDb.getInstance(context).workoutTemplateDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    private val workoutTemplateExerciseDao = AppDb.getInstance(context).workoutTemplateExerciseDao()

    override suspend fun insertWorkoutTemplate(workoutTemplate: WorkoutTemplate): Long = withContext(Dispatchers.IO){
        val newId = workoutTemplateDao.insertWorkoutTemplate(workoutTemplate)

        val q = SyncQueue(
            tableName = "workoutTemplates",
            localId = newId.toInt()
        )
        syncQueueDao.insertSyncQueue(q)
        newId
    }

    override suspend fun insertAllWorkoutTemplates(workoutTemplates: List<WorkoutTemplate>): List<Long> = withContext(Dispatchers.IO){
        val newId = workoutTemplateDao.insertAllWorkoutTemplates(workoutTemplates)
        for(i in newId){
            val q = SyncQueue(
                tableName = "workout_templates",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        newId
    }

    override suspend fun updateWorkoutTemplate(workoutTemplate: WorkoutTemplate) = withContext(Dispatchers.IO){
        workoutTemplateDao.updateWorkoutTemplate(workoutTemplate)
        if(syncQueueDao.getSyncQueueByTableName(workoutTemplate.workoutTemplateId,"workout_templates") == null){
            val q = SyncQueue(
                tableName = "workout_templates",
                localId = workoutTemplate.workoutTemplateId,
                globalId = workoutTemplate.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteWorkoutTemplate(workoutTemplate: WorkoutTemplate) = withContext(Dispatchers.IO){
        if(workoutTemplate.globalId != null){
            val q = SyncQueue(
                tableName = "workout_templates",
                localId = workoutTemplate.workoutTemplateId,
                globalId = workoutTemplate.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(workoutTemplate.workoutTemplateId,"workout_templates")
            if(existing != null){
                syncQueueDao.deleteSyncQueue(existing)
            }
        }
        workoutTemplateDao.deleteWorkoutTemplate(workoutTemplate)
    }

    override fun getWorkoutTemplateById(id: Int): Flow<WorkoutTemplate> {
        return workoutTemplateDao.getWorkoutTemplateById(id)
    }

    override fun getAllWorkoutTemplates(): Flow<List<WorkoutTemplate>> {
        return workoutTemplateDao.getAllWorkoutTemplates()
    }

    override fun getTemplateWithExercises(id: Int): Flow<WorkoutTemplateWithExercises> {
        return workoutTemplateDao.getTemplateWithExercises(id)
    }

    override fun getTemplateWithWorkouts(id: Int): Flow<WorkoutTemplateWithWorkouts> {
        return workoutTemplateDao.getTemplateWithWorkouts(id)
    }

    override fun getAllTemplatesWithExercises(): Flow<List<WorkoutTemplateWithExercises>> {
        return workoutTemplateDao.getAllTemplatesWithExercises()
    }

    override fun getAllTemplatesWithWorkouts(): Flow<List<WorkoutTemplateWithWorkouts>> {
        return workoutTemplateDao.getAllTemplatesWithWorkouts()
    }

    override suspend fun insertWorkoutTemplateExercise(workoutTemplateExercise: WorkoutTemplateExercise) = withContext(
        Dispatchers.IO){
        workoutTemplateExerciseDao.insertWorkoutTemplateExercise(workoutTemplateExercise)
    }

    override suspend fun insertAllWorkoutTemplateExercises(workoutTemplateExercises: List<WorkoutTemplateExercise>) = withContext(Dispatchers.IO){
        workoutTemplateExerciseDao.insertAllWorkoutTemplateExercises(workoutTemplateExercises)
    }
}