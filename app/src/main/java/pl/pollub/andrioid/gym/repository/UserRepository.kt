package pl.pollub.andrioid.gym.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.pollub.andrioid.gym.db.AppDb

import pl.pollub.andrioid.gym.db.dao.UserDao
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.relationships.UserWithBodyMeasurements
import pl.pollub.andrioid.gym.db.relationships.UserWithExercises
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndMuscleGroups
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndSets
import pl.pollub.andrioid.gym.db.relationships.UserWithSyncQueues
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkoutTemplates
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkouts
import pl.pollub.andrioid.gym.network.ApiClient
import pl.pollub.andrioid.gym.network.TokenManager
import pl.pollub.andrioid.gym.network.dto.reguest.LoginRequest

class UserRepository(context: Context){
    private val userDao = AppDb.getInstance(context).userDao()
    private val api = ApiClient.create(context)


    private val tokenManager = TokenManager(context)

    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = api.login(LoginRequest(username = username, password = password))
            tokenManager.saveToken(response.token)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getToken(): String? = tokenManager.getToken()

    fun logout() = tokenManager.clearToken()
    suspend fun getLastSync(): String? {
        return userDao.getLastSync()
    }

    suspend fun getLoggedInUserId(): Int? {
        return userDao.getLoggedInUserId()
    }

    suspend fun updateLastSync(lastSync: String) {
        userDao.updateLastSync(lastSync)
    }


    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO){
        val newId = userDao.insertUser(user)
        newId
    }

    suspend fun insertAllUsers(users: List<User>): List<Long> = withContext(Dispatchers.IO){
        val newId = userDao.insertAllUsers(users)
        newId
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO){
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO){
        userDao.deleteUser(user)
    }

    fun getUserById(id: Int): Flow<User> {
        return userDao.getUserById(id)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    fun getUserWithWorkoutsById(id: Int): Flow<UserWithWorkouts> {
        return userDao.getUserWithWorkoutsById(id)
    }

    fun getUserWithExercisesById(id: Int): Flow<UserWithExercises> {
        return userDao.getUserWithExercisesById(id)
    }

    fun getUserWithWorkoutTemplatesById(id: Int): Flow<UserWithWorkoutTemplates> {
        return userDao.getUserWithWorkoutTemplatesById(id)
    }

    fun getUserWithBodyMeasurementsById(id: Int): Flow<UserWithBodyMeasurements> {
        return userDao.getUserWithBodyMeasurementsById(id)
    }

    fun getUserWithSyncQueuesById(id: Int): UserWithSyncQueues {
        return userDao.getUserWithSyncQueuesById(id)
    }

    fun getUserWithExercisesAndMuscleGroupsById(id: Int): Flow<UserWithExercisesAndMuscleGroups> {
        return userDao.getUserWithExercisesAndMuscleGroupsById(id)
    }

    fun getUserWithExercisesAndSetsById(id: Int): Flow<UserWithExercisesAndSets> {
        return userDao.getUserWithExercisesAndSetsById(id)
    }
}
