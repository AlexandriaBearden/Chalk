package com.alexandria.chalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexandria.chalk.ui.theme.ChalkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChalkTheme {

                val context = LocalContext.current

                val database = remember {
                    ChalkDatabase.getDatabase(context)
                }

                val savedGymDao = remember {
                    database.savedGymDao()
                }

                var currentScreen by remember {
                    mutableStateOf(ChalkScreen.HOME)
                }

                val coroutineScope = rememberCoroutineScope()

                var selectedWorkoutOptions by remember {
                    mutableStateOf(setOf<String>())
                }

                val savedGyms by savedGymDao
                    .getAllSavedGyms()
                    .collectAsState(initial = emptyList())

                val savedGymNames = savedGyms
                    .map { gym -> gym.name }
                    .toSet()

                var selectedGym by remember {
                    mutableStateOf<Gym?>(null)
                }

                val gyms = listOf(
                    Gym(
                        "Atlas Strength Club",
                        "0.8 Miles Away",
                        listOf("Strength", "Day Pass", "Recovery")
                    ),
                    Gym(
                        "Form Studio",
                        "1.4 Miles Away",
                        listOf("Pilates", "Group Fitness")
                    ),
                    Gym(
                        "The Training Room",
                        "2.1 Miles Away",
                        listOf("Strength", "Group Fitness", "Day Pass")
                    )
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (currentScreen) {
                        ChalkScreen.HOME -> {
                            ChalkHomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onFindGyms = { selectedOptions ->
                                    selectedWorkoutOptions = selectedOptions
                                    currentScreen = ChalkScreen.RESULTS
                                },
                                onSavedGymsClick = {
                                    currentScreen = ChalkScreen.SAVED
                                }
                            )
                        }

                        ChalkScreen.RESULTS -> {
                            GymResultsScreen(
                                selectedOptions = selectedWorkoutOptions,
                                savedGymNames = savedGymNames,
                                onSaveGym = { gymName ->
                                    coroutineScope.launch {
                                        if (gymName in savedGymNames) {
                                            savedGyms
                                                .firstOrNull { it.name == gymName }
                                                ?.let { savedGymDao.deleteGym(it) }
                                        } else {
                                            gyms
                                                .firstOrNull { it.name == gymName }
                                                ?.let { gym ->
                                                    savedGymDao.saveGym(
                                                        SavedGymEntity(
                                                            name = gym.name,
                                                            location = gym.location
                                                        )
                                                    )
                                                }
                                        }
                                    }
                                },
                                onBack = {
                                    currentScreen = ChalkScreen.HOME
                                },
                                onGymClick = { gym ->
                                    selectedGym = gym
                                    currentScreen = ChalkScreen.DETAILS
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        ChalkScreen.DETAILS -> {
                            selectedGym?.let { gym ->
                                GymDetailsScreen(
                                    gym = gym,
                                    isSaved = gym.name in savedGymNames,
                                    onSaveClick = {
                                        coroutineScope.launch {
                                            if (gym.name in savedGymNames) {
                                                savedGyms
                                                    .firstOrNull { it.name == gym.name }
                                                    ?.let { savedGymDao.deleteGym(it) }
                                            } else {
                                                savedGymDao.saveGym(
                                                    SavedGymEntity(
                                                        name = gym.name,
                                                        location = gym.location
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onBack = {
                                        currentScreen = ChalkScreen.RESULTS
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }

                        ChalkScreen.SAVED -> {
                            SavedGymsScreen(
                                gyms = gyms.filter { gym ->
                                    gym.name in savedGymNames
                                                   },
                                onGymClick = { gym ->
                                    selectedGym = gym
                                    currentScreen = ChalkScreen.DETAILS
                                },
                                onBack = {
                                    currentScreen = ChalkScreen.HOME
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class ChalkScreen {
    HOME,
    RESULTS,
    DETAILS,
    SAVED
}

data class Gym(
    val name: String,
    val location: String,
    val features: List<String>
)

@Composable
fun ChalkHomeScreen(
    modifier: Modifier = Modifier,
    onFindGyms: (Set<String>) -> Unit,
    onSavedGymsClick: () -> Unit
) {
    val workoutOptions = listOf(
        "Strength",
        "Pilates",
        "Group Fitness",
        "Recovery",
        "Day Pass"
    )

    var selectedOptions by remember {
        mutableStateOf(setOf<String>())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "CHALK",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "What are you looking for today?",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        workoutOptions.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { option ->
                    FilterChip(
                        selected = option in selectedOptions,
                        onClick = {
                            selectedOptions =
                                if (option in selectedOptions) {
                                    selectedOptions - option
                                } else {
                                    selectedOptions + option
                                }
                        },
                        label = {
                            Text(option)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = onSavedGymsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Saved Gyms")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onFindGyms(selectedOptions)
            },
            enabled = selectedOptions.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Gyms")
        }
    }
}

@Composable
fun GymResultsScreen(
    selectedOptions: Set<String>,
    savedGymNames: Set<String>,
    onSaveGym: (String) -> Unit,
    onGymClick: (Gym) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gyms = listOf(
        Gym(
            name = "Atlas Strength Club",
            location = "0.8 miles away",
            features = listOf("Strength", "Day Pass", "Recovery")
        ),
        Gym(
            name = "Form Studio",
            location = "1.4 miles away",
            features = listOf("Pilates", "Group Fitness")
        ),
        Gym(
            name = "The Training Room",
            location = "2.1 miles away",
            features = listOf("Strength", "Group Fitness", "Day Pass")
        )
    )

    val matchingGyms = gyms.filter { gym ->
        gym.features.any { feature ->
            feature in selectedOptions
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gyms for you",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Based on: ${selectedOptions.joinToString()}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(matchingGyms) { gym ->
                GymResultCard(
                    gym = gym,
                    isSaved = gym.name in savedGymNames,
                    onSaveClick = {
                        onSaveGym(gym.name)
                    },
                    onGymClick = {
                        onGymClick(gym)
                    }
                )
            }
        }
    }
}

@Composable
fun GymResultCard(
    gym: Gym,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onGymClick: () -> Unit
) {
    Card(
        onClick = onGymClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = gym.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = gym.location,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = gym.features.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSaved) {
                        "Saved"
                    } else {
                        "Save Gym"
                    }
                )
            }
        }
    }
}

@Composable
fun GymDetailsScreen(
    gym: Gym,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = gym.name,
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = gym.location,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = gym.features.joinToString("\n"),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSaved) {
                    "Saved"
                } else { "Save Gym" }
            )
        }
    }
}

@Composable
fun SavedGymsScreen(
    gyms: List<Gym>,
    onGymClick: (Gym) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Saved Gyms",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (gyms.isEmpty()) {
            Text(
                text = "You haven't saved any gyms yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(gyms) { gym ->
                    Card(
                        onClick = {
                            onGymClick(gym)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = gym.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = gym.location,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = gym.features.joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}