package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb
import pl.pollub.andrioid.gym.db.dao.SetDao
import pl.pollub.andrioid.gym.db.entity.Set

class SetRepository(context: Context): SetDao {
    private val setDao = AppDb.getInstance(context).setDao()

    override suspend fun insertSet(set: Set): Long = withContext(Dispatchers.IO){
        val newId = setDao.insertSet(set)
        newId
    }

    override suspend fun insertSets(sets: List<Set>): List<Long> = withContext(Dispatchers.IO){
        val newId = setDao.insertSets(sets)
        newId
    }

    override suspend fun updateSet(set: Set) = withContext(Dispatchers.IO){
        setDao.updateSet(set)
    }

    override suspend fun deleteSet(set: Set) = withContext(Dispatchers.IO){
        setDao.deleteSet(set)
    }

    override fun getSetById(id: Int): Flow<Set> {
        return setDao.getSetById(id)
    }

    override fun getAllSets(): Flow<List<Set>> {
        return setDao.getAllSets()
    }
}