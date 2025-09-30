package pl.pollub.andrioid.gym.repository;

import android.content.Context
import kotlinx.coroutines.flow.Flow

import pl.pollub.andrioid.gym.db.dao.UserDao
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.relationships.UserWithBodyMeasurements
import pl.pollub.andrioid.gym.db.relationships.UserWithExercises
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndMuscleGroups
import pl.pollub.andrioid.gym.db.relationships.UserWithExercisesAndSets
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkoutTemplates
import pl.pollub.andrioid.gym.db.relationships.UserWithWorkouts

class UserRepository(context: Context):UserDao{
    override suspend fun insertUser(user: User): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertAllUsers(users: List<User>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun getUserById(id: Int): Flow<User> {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getUserWithWorkouts(id: Int): Flow<UserWithWorkouts> {
        TODO("Not yet implemented")
    }

    override fun getUserWithExercises(id: Int): Flow<UserWithExercises> {
        TODO("Not yet implemented")
    }

    override fun getUserWithWorkoutTemplates(id: Int): Flow<UserWithWorkoutTemplates> {
        TODO("Not yet implemented")
    }

    override fun getUserWithBodyMeasurements(id: Int): Flow<UserWithBodyMeasurements> {
        TODO("Not yet implemented")
    }

    override fun getUserWithExercisesAndMuscleGroups(id: Int): Flow<UserWithExercisesAndMuscleGroups> {
        TODO("Not yet implemented")
    }

    override fun getUserWithExercisesAndSets(id: Int): Flow<UserWithExercisesAndSets> {
        TODO("Not yet implemented")
    }
}
