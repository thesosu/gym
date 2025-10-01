package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.MuscleGroupDao
import pl.pollub.andrioid.gym.db.entity.MuscleGroup
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.relationships.MuscleGroupWithExercises

class MuscleGroupRepository(context: Context): MuscleGroupDao {
    private val muscleGroupDao = AppDb.getInstance(context).muscleGroupDao()
    private val syncQueueDao = AppDb.getInstance(context).syncQueueDao()
    override suspend fun insertMuscleGroup(muscleGroup: MuscleGroup): Long {
        val newId = muscleGroupDao.insertMuscleGroup(muscleGroup)
        val q =SyncQueue(
            tableName = "muscle_groups",
            localId = newId.toInt()
        )
        syncQueueDao.insertSyncQueue(q)
        return newId
    }

    override suspend fun insertMuscleGroups(muscleGroups: List<MuscleGroup>): List<Long> {
        val newId = muscleGroupDao.insertMuscleGroups(muscleGroups)
        for(i in newId){
            val q = SyncQueue(
                tableName = "muscle_groups",
                localId = i.toInt()
            )
            syncQueueDao.insertSyncQueue(q)
        }
        return newId
    }

    override suspend fun updateMuscleGroup(muscleGroup: MuscleGroup) {
        muscleGroupDao.updateMuscleGroup(muscleGroup)
        if(syncQueueDao.getSyncQueueByTableName(muscleGroup.muscleGroupId,"muscle_groups") == null){
            val q = SyncQueue(
                tableName = "muscle_groups",
                localId = muscleGroup.muscleGroupId,
                globalId = muscleGroup.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }
    }

    override suspend fun deleteMuscleGroup(muscleGroup: MuscleGroup) {
        if(muscleGroup.globalId != null){
            val q = SyncQueue(
                tableName = "muscle_groups",
                localId = muscleGroup.muscleGroupId,
                globalId = muscleGroup.globalId
            )
            syncQueueDao.insertSyncQueue(q)
        }else{
            val existing = syncQueueDao.getSyncQueueByTableName(muscleGroup.muscleGroupId,"muscle_groups")
            if(existing != null){
                syncQueueDao.deleteSyncQueue(existing)
            }
        }
        muscleGroupDao.deleteMuscleGroup(muscleGroup)
    }

    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
        return muscleGroupDao.getAllMuscleGroups()
    }

    override fun getMuscleGroupById(id: Int): Flow<MuscleGroup> {
        return muscleGroupDao.getMuscleGroupById(id)
    }

    override fun getMuscleGroupWithExercises(id: Int): Flow<MuscleGroupWithExercises> {
        return muscleGroupDao.getMuscleGroupWithExercises(id)
    }

    override fun getAllMuscleGroupsWithExercises(): Flow<List<MuscleGroupWithExercises>> {
        return muscleGroupDao.getAllMuscleGroupsWithExercises()
    }
}