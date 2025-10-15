package pl.pollub.andrioid.gym.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.pollub.andrioid.gym.db.dao.BodyMeasurementDao
import pl.pollub.andrioid.gym.db.dao.ExerciseDao
import pl.pollub.andrioid.gym.db.dao.ExerciseMuscleGroupDao
import pl.pollub.andrioid.gym.db.dao.MuscleGroupDao
import pl.pollub.andrioid.gym.db.dao.SetDao
import pl.pollub.andrioid.gym.db.dao.SyncQueueDao
import pl.pollub.andrioid.gym.db.dao.UserDao
import pl.pollub.andrioid.gym.db.dao.WorkoutDao
import pl.pollub.andrioid.gym.db.dao.WorkoutExerciseDao
import pl.pollub.andrioid.gym.db.dao.WorkoutTemplateDao
import pl.pollub.andrioid.gym.db.dao.WorkoutTemplateExerciseDao
import pl.pollub.andrioid.gym.db.entity.BodyMeasurement
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.MuscleGroup
import pl.pollub.andrioid.gym.db.entity.Set
import pl.pollub.andrioid.gym.db.entity.SyncQueue
import pl.pollub.andrioid.gym.db.entity.User
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplate
import pl.pollub.andrioid.gym.db.entity.WorkoutTemplateExercise


@Database(entities = [
    User::class,
    Exercise::class,
    MuscleGroup::class,
    ExerciseMuscleGroup::class,
    WorkoutTemplate::class,
    WorkoutTemplateExercise::class,
    Workout::class,
    WorkoutExercise::class,
    Set::class,
    BodyMeasurement::class,
    SyncQueue::class
], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun muscleGroupDao(): MuscleGroupDao
    abstract fun exerciseMuscleGroupDao(): ExerciseMuscleGroupDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun workoutTemplateExerciseDao(): WorkoutTemplateExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun setDao(): SetDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun syncQueueDao(): SyncQueueDao

}
object AppDb{
    @Volatile
    private var db: AppDatabase? = null
    fun getInstance(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "gym"
            )
                .addCallback(AppDatabaseCallback(context))
                .build()
            db = instance
            instance
        }
    }
}

class AppDatabaseCallback(private val context: Context): RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            prepopulateDatabase(context)
        }
    }

    private suspend fun prepopulateDatabase(context: Context) {
        val database = AppDb.getInstance(context)
        val muscleGroupDao = database.muscleGroupDao()

        val groups = listOf(
            MuscleGroup(name = "Klatka piersiowa"),
            MuscleGroup(name = "Plecy"),
            MuscleGroup(name = "Nogi"),
            MuscleGroup(name = "Ramiona"),
            MuscleGroup(name = "Biceps"),
            MuscleGroup(name = "Triceps")
        )

         muscleGroupDao.insertMuscleGroups(groups)


    }
}
