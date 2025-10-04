package pl.pollub.andrioid.gym.repository;

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb

import pl.pollub.andrioid.gym.db.dao.UserDao
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.relationships.UserWithBodyMeasurements
import pl.pollub.andrioid.gym.db.relationships.UserWithExercises
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndMuscleGroups
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndSets
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkoutTemplates
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkouts

class UserRepository(context: Context):UserDao{
    private val userDao = AppDb.getInstance(context).userDao()
    override suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO){
        val newId = userDao.insertUser(user)
        newId
    }

    override suspend fun insertAllUsers(users: List<User>): List<Long> = withContext(Dispatchers.IO){
        val newId = userDao.insertAllUsers(users)
        newId
    }

    override suspend fun updateUser(user: User) = withContext(Dispatchers.IO){
        userDao.updateUser(user)
    }

    override suspend fun deleteUser(user: User) = withContext(Dispatchers.IO){
        userDao.deleteUser(user)
    }

    override fun getUserById(id: Int): Flow<User> {
        return userDao.getUserById(id)
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    override fun getUserWithWorkoutsById(id: Int): Flow<UserWithWorkouts> {
        return userDao.getUserWithWorkoutsById(id)
    }

    override fun getUserWithExercisesById(id: Int): Flow<UserWithExercises> {
        return userDao.getUserWithExercisesById(id)
    }

    override fun getUserWithWorkoutTemplatesById(id: Int): Flow<UserWithWorkoutTemplates> {
        return userDao.getUserWithWorkoutTemplatesById(id)
    }

    override fun getUserWithBodyMeasurementsById(id: Int): Flow<UserWithBodyMeasurements> {
        return userDao.getUserWithBodyMeasurementsById(id)
    }

    override fun getUserWithExercisesAndMuscleGroupsById(id: Int): Flow<UserWithExercisesAndMuscleGroups> {
        return userDao.getUserWithExercisesAndMuscleGroupsById(id)
    }

    override fun getUserWithExercisesAndSetsById(id: Int): Flow<UserWithExercisesAndSets> {
        return userDao.getUserWithExercisesAndSetsById(id)
    }
}
