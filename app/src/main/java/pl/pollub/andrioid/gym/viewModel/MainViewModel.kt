package pl.pollub.andrioid.gym.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.MuscleGroup
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.repository.BodyMeasurementRepository
import pl.pollub.andrioid.gym.repository.ExerciseRepository
import pl.pollub.andrioid.gym.repository.MuscleGroupRepository
import pl.pollub.andrioid.gym.repository.SetRepository
import pl.pollub.andrioid.gym.repository.SyncQueueRepository
import pl.pollub.andrioid.gym.repository.UserRepository
import pl.pollub.andrioid.gym.repository.WorkoutRepository
import pl.pollub.andrioid.gym.repository.WorkoutTemplateRepository

class MainViewModel(app: Application): AndroidViewModel(app) {
    private val bodyMeasurementRepository = BodyMeasurementRepository(app.applicationContext)
    private val exerciseRepository = ExerciseRepository(app.applicationContext)
    private val muscleGroupRepository = MuscleGroupRepository(app.applicationContext)
    private val setRepository = SetRepository(app.applicationContext)
    private val syncQueueRepository = SyncQueueRepository(app.applicationContext)
    private val userRepository = UserRepository(app.applicationContext)
    private val workoutRepository = WorkoutRepository(app.applicationContext)
    private val workoutTemplateRepository = WorkoutTemplateRepository(app.applicationContext)


    private val _exercise = MutableStateFlow<Exercise?>(null)
    val exercise = _exercise.asStateFlow()

    fun add(){
        viewModelScope.launch {
                syncQueueRepository.uploadToServer()

        }
    }
    private val _loginState =  MutableStateFlow(!userRepository.getToken().isNullOrEmpty())
    val loginState: StateFlow<Boolean> get() = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val success = userRepository.login(username, password)
            _loginState.value = success
        }
    }

    fun getToken(): String? {
        return userRepository.getToken()
    }


    fun logout() {
        userRepository.logout()
        _loginState.value = false
    }

    fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseRepository.getAllExercises()
    }
    suspend fun insertExercise(exercise: Exercise): Long {

        return exerciseRepository.insertExercise(exercise)

    }
    suspend fun insertMuscleGroup(muscleGroup: MuscleGroup): Long {

        return muscleGroupRepository.insertMuscleGroup(muscleGroup)

    }
    suspend fun insertUser(user: User): Long{
        return userRepository.insertUser(user)
    }
    suspend fun insertExerciseMuscleGroup(exerciseMuscleGroup: ExerciseMuscleGroup){
        exerciseRepository.insertExerciseMuscleGroup(exerciseMuscleGroup)
    }
    fun getUser(): Flow<List<User>> {
        return userRepository.getAllUsers()
    }

    fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
        return muscleGroupRepository.getAllMuscleGroups()
    }
    suspend fun deleteMuscleGroup(muscleGroup: MuscleGroup){
        muscleGroupRepository.deleteMuscleGroup(muscleGroup)
    }
    suspend fun deleteExercise(exercise: Exercise){
        exerciseRepository.deleteExercise(exercise)
    }


}