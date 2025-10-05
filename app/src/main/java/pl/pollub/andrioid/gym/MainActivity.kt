package pl.pollub.andrioid.gym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.pollub.andrioid.gym.ui.theme.GymTheme
import pl.pollub.andrioid.gym.viewModel.MainViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.pollub.andrioid.gym.db.entity.Exercise
import pl.pollub.andrioid.gym.db.entity.ExerciseMuscleGroup
import pl.pollub.andrioid.gym.db.entity.MuscleGroup
import pl.pollub.andrioid.gym.db.entity.User


class MainActivity : ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel> ()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    Greeting(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(mainViewModel:MainViewModel) {
    val exercises by mainViewModel.getAllExercises().collectAsState(initial = emptyList())
    val muscleGroups by mainViewModel.getAllMuscleGroups().collectAsState(initial = emptyList())
    val users by mainViewModel.getUser().collectAsState(initial = emptyList())
    val loginState by mainViewModel.loginState.collectAsState()

    val usernameState = remember {mutableStateOf("") }
    val passwordState = remember {mutableStateOf("")}

    val time = System.currentTimeMillis()
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("===============================")

        Text(exercises.toString())
        Text("===============================")

        Text(muscleGroups.toString())
        Text("===============================")
        if (!loginState) {
            Text("Logowanie")
            OutlinedTextField(
                value = usernameState.value,
                onValueChange = { usernameState.value = it },
                label = { Text("Username") },
                modifier = Modifier.padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Button(
                onClick = {
                    scope.launch {
                        mainViewModel.login(usernameState.value, passwordState.value)
                    }
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            ) {
                Text("Zaloguj")
            }
        } else {
            Text("Zalogowany")
            Button(
                onClick = { mainViewModel.logout() },
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            ) {
                Text("Wyloguj")
            }
        }


        Button(
            onClick = {
                scope.launch {

                    if (users.isEmpty()) {
                        val uId = mainViewModel.insertUser(User( userName = "user", sex = true, email = "email"))
                        val eId = mainViewModel.insertExercise(Exercise(userId = uId.toInt(), name = " ex$time", description = "ok"))
                        val mgId = mainViewModel.insertMuscleGroup(MuscleGroup(name = "mg $time"))
                        mainViewModel.insertExerciseMuscleGroup(ExerciseMuscleGroup(muscleGroupId = mgId.toInt(), exerciseId = eId.toInt()))
                    } else {
                        val uId = users.first().userId
                        val eId = mainViewModel.insertExercise(Exercise(userId = uId, name = " ex$time", description = "ok"))
                        val mgId = mainViewModel.insertMuscleGroup(MuscleGroup(name = "mg $time"))
                        mainViewModel.insertExerciseMuscleGroup(ExerciseMuscleGroup(muscleGroupId = mgId.toInt(), exerciseId = eId.toInt()))

                    }
                }

            },
            modifier = Modifier
                .width(120.dp)
                .height(40.dp)
        ) { Text("dodaj") }
        Button(onClick = {
            scope.launch {
                mainViewModel.deleteExercise(exercises.last())
            }

        },modifier = Modifier
            .width(120.dp)
            .height(40.dp)) { Text("usuń")
        }
        Button(onClick = {
            scope.launch {
                mainViewModel.add()
            }

        },modifier = Modifier
            .width(120.dp)
            .height(40.dp)) { Text("Wyslij")
        }
    }
}

