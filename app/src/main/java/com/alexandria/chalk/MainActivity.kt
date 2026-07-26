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
                        name = "Iron House Fitness",
                        location = "Downtown Austin",
                        rating = 4.8,
                        distanceMiles = 0.7,
                        dayPassPrice = 20,
                        isOpen = true,
                        workoutTypes = listOf(
                            "Strength",
                            "Cardio",
                            "CrossFit"
                        ),
                        amenities = listOf(
                            "Day Pass",
                            "Showers",
                            "Lockers",
                            "Parking"
                        )
                    ),

                    Gym(
                        name = "Form Pilates Studio",
                        location = "South Congress",
                        rating = 4.9,
                        distanceMiles = 1.3,
                        dayPassPrice = 28,
                        isOpen = true,
                        workoutTypes = listOf(
                            "Pilates",
                            "Yoga",
                            "Mobility"
                        ),
                        amenities = listOf(
                            "Day Pass",
                            "Showers",
                            "Towels",
                            "Lockers"
                        )
                    ),

                    Gym(
                        name = "District Training Club",
                        location = "East Austin",
                        rating = 4.6,
                        distanceMiles = 2.1,
                        dayPassPrice = 18,
                        isOpen = false,
                        workoutTypes = listOf(
                            "Strength",
                            "HIIT",
                            "Group Classes"
                        ),
                        amenities = listOf(
                            "Day Pass",
                            "Showers",
                            "Parking",
                            "Sauna"
                        )
                    ),

                    Gym(
                        name = "Sol Wellness",
                        location = "West Austin",
                        rating = 4.7,
                        distanceMiles = 3.4,
                        dayPassPrice = 30,
                        isOpen = true,
                        workoutTypes = listOf(
                            "Yoga",
                            "Pilates",
                            "Cardio"
                        ),
                        amenities = listOf(
                            "Day Pass",
                            "Towels",
                            "Showers",
                            "Sauna",
                            "Pool"
                        )
                    ),

                    Gym(
                        name = "Forge Strength Lab",
                        location = "North Austin",
                        rating = 4.5,
                        distanceMiles = 4.2,
                        dayPassPrice = 15,
                        isOpen = true,
                        workoutTypes = listOf(
                            "Strength",
                            "Powerlifting",
                            "CrossFit"
                        ),
                        amenities = listOf(
                            "Day Pass",
                            "Lockers",
                            "Parking"
                        )
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
                                gyms = gyms,
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

    val rating: Double,
    val distanceMiles: Double,

    val dayPassPrice: Int,
    val isOpen: Boolean,

    val workoutTypes: List<String>,
    val amenities: List<String>
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
        "Yoga",
        "Cardio",
        "CrossFit",
        "Group Classes",
        "Day Pass",
        "Showers"
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
    gyms: List<Gym>,
    selectedOptions: Set<String>,
    savedGymNames: Set<String>,
    onSaveGym: (String) -> Unit,
    onGymClick: (Gym) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rankedGyms = gyms
        .map { gym ->
            val allFeatures = gym.workoutTypes + gym.amenities

            val matchCount = selectedOptions.count { selectedOption ->
                selectedOption in allFeatures
            }

            gym to matchCount
        }
        .filter { (_, matchCount) ->
            matchCount > 0
        }
        .sortedWith(
            compareByDescending<Pair<Gym, Int>> { (_, matchCount) ->
                matchCount
            }.thenByDescending { (gym, _) ->
                gym.rating
            }
        )

    val highestMatchCount = rankedGyms
        .maxOfOrNull { (_, matchCount) -> matchCount }
        ?: 0

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

        if (rankedGyms.isEmpty()) {
            Text(
                text = "No gyms match your selected preferences.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try going back and selecting different workout options.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(rankedGyms) { (gym, matchCount) ->
                    GymResultCard(
                        gym = gym,
                        selectedOptions = selectedOptions,
                        matchCount = matchCount,
                        isBestMatch = matchCount == highestMatchCount,
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
}

@Composable
fun GymResultCard(
    gym: Gym,
    selectedOptions: Set<String>,
    matchCount: Int,
    isBestMatch: Boolean,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onGymClick: () -> Unit
) {
    val allFeatures = gym.workoutTypes + gym.amenities

    val matchedFeatures = selectedOptions.filter { selectedOption ->
        selectedOption in allFeatures
    }

    val matchPercentage = if (selectedOptions.isNotEmpty()) {
        (matchCount * 100) / selectedOptions.size
    } else {
        0
    }

    Card(
        onClick = onGymClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (isBestMatch) {
                Text(
                    text = "BEST MATCH",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = gym.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "★ ${gym.rating}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = gym.location,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${gym.distanceMiles} miles away",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (gym.isOpen) {
                        "Open now"
                    } else {
                        "Closed"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "$${gym.dayPassPrice} day pass",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = gym.workoutTypes.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "$matchPercentage% match",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$matchCount of ${selectedOptions.size} preferences matched",
                style = MaterialTheme.typography.bodySmall
            )

            if (matchedFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Matches: ${matchedFeatures.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "★ ${gym.rating} • ${gym.distanceMiles} miles away",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (gym.isOpen) {
                "Open now"
            } else {
                "Currently closed"
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Day Pass",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$${gym.dayPassPrice}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Workout Types",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = gym.workoutTypes.joinToString("\n") { workoutType ->
                "• $workoutType"
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Amenities",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = gym.amenities.joinToString("\n") { amenity ->
                "• $amenity"
            },
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
                } else {
                    "Save Gym"
                }
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Save a gym from your results and it will appear here.",
                style = MaterialTheme.typography.bodyMedium
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = gym.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "★ ${gym.rating}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = gym.location,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${gym.distanceMiles} miles away",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = gym.workoutTypes.joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}