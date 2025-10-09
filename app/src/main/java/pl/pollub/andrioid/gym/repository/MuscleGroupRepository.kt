package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.MuscleGroupDao
import pl.pollub.andrioid.gym.db.entity.MuscleGroup
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.relationships.MuscleGroupWithExercises

class MuscleGroupRepository(context: Context): MuscleGroupDao {
    private val muscleGroupDao = AppDb.getInstance(context).muscleGroupDao()

    override suspend fun insertMuscleGroup(muscleGroup: MuscleGroup): Long = withContext(Dispatchers.IO){
        val newId = muscleGroupDao.insertMuscleGroup(muscleGroup)
        newId
    }

    override suspend fun insertMuscleGroups(muscleGroups: List<MuscleGroup>): List<Long> = withContext(Dispatchers.IO){
        val newId = muscleGroupDao.insertMuscleGroups(muscleGroups)
        newId
    }

    override suspend fun updateMuscleGroup(muscleGroup: MuscleGroup) = withContext(Dispatchers.IO){
        muscleGroupDao.updateMuscleGroup(muscleGroup)

    }

    override suspend fun deleteMuscleGroup(muscleGroup: MuscleGroup) = withContext(Dispatchers.IO){
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