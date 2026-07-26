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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
                    mutableStateOf(ChalkScreen.LANDING)
                }

                val coroutineScope = rememberCoroutineScope()

                var selectedWorkoutOptions by remember {
                    mutableStateOf(setOf<String>())
                }

                var selectedDestination by remember {
                    mutableStateOf("")
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
                        ChalkScreen.LANDING -> {
                            ChalkLandingScreen(
                                onFinished = {
                                    currentScreen = ChalkScreen.HOME
                                }
                            )
                        }

                        ChalkScreen.HOME -> {
                            ChalkHomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onFindGyms = { destination, selectedOptions ->
                                    selectedDestination = destination
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
                                destination = selectedDestination,
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
    LANDING,
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
fun ChalkLandingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 700)
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHALK",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Find your fit.",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(40.dp))

                TextButton(
                    onClick = onFinished
                ) {
                    Text(
                        text = "Begin",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "→",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ChalkHomeScreen(
    modifier: Modifier = Modifier,
    onFindGyms: (String, Set<String>) -> Unit,
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

    var destination by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CHALK",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Find the right gym,\nwherever you travel.",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Choose a destination and tell us what matters for your workout.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Destination",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = { newDestination ->
                    destination = newDestination
                },
                placeholder = {
                    Text("Austin, TX")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Workout Preferences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select everything you want included.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            workoutOptions.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSavedGymsClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "View Saved Gyms",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                onFindGyms(
                    destination,
                    selectedOptions)
            },
            enabled = destination.isNotBlank() &&
                    selectedOptions.isNotEmpty(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = "Find Gyms",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GymResultsScreen(
    gyms: List<Gym>,
    destination: String,
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
            text = "Gyms in $destination",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ranked around your workout preferences",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = selectedOptions.joinToString(" • "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            if (isBestMatch) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "BEST MATCH",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = gym.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = gym.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${gym.distanceMiles} miles away",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${gym.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.55f
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (gym.isOpen) {
                                "Open now"
                            } else {
                                "Currently closed"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (gym.isOpen) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Current availability",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "$${gym.dayPassPrice}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Day pass",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Workout options",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            gym.workoutTypes.chunked(3).forEach { workoutRow ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    workoutRow.forEach { workoutType ->
                        FeaturePill(
                            text = workoutType,
                            isHighlighted = workoutType in matchedFeatures
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "$matchPercentage% match",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$matchCount of ${selectedOptions.size} preferences matched",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (matchedFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Matched preferences",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                matchedFeatures.chunked(3).forEach { featureRow ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        featureRow.forEach { feature ->
                            FeaturePill(
                                text = feature,
                                isHighlighted = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSaved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            ) {
                Text(
                    text = if (isSaved) {
                        "Saved"
                    } else {
                        "Save Gym"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FeaturePill(
    text: String,
    isHighlighted: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isHighlighted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isHighlighted) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            )
        )
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
    val travelerBenefits = buildList {
        if ("Day Pass" in gym.amenities) {
            add("No long-term membership required")
        }

        if ("Lockers" in gym.amenities) {
            add("Secure storage while you train")
        }

        if ("Showers" in gym.amenities) {
            add("Easy to fit into a travel day")
        }

        if ("Towels" in gym.amenities) {
            add("One less thing to pack")
        }

        if ("Parking" in gym.amenities) {
            add("Convenient for travelers with a rental car")
        }

        if ("Sauna" in gym.amenities) {
            add("Recovery amenities available after your workout")
        }

        if ("Pool" in gym.amenities) {
            add("Additional low-impact training and recovery options")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                TextButton(
                    onClick = onBack
                ) {
                    Text("Back")
                }
            }

            item {
                Column {
                    Text(
                        text = gym.name,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = gym.location,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${gym.rating} rating  •  ${gym.distanceMiles} miles away",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.55f
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (gym.isOpen) {
                                    "Open now"
                                } else {
                                    "Currently closed"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (gym.isOpen) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Current availability",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "$${gym.dayPassPrice}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Day pass",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                DetailSectionCard(
                    title = "Workout Options",
                    items = gym.workoutTypes
                )
            }

            item {
                DetailSectionCard(
                    title = "Amenities",
                    items = gym.amenities
                )
            }

            if (travelerBenefits.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = 0.5f
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Why travelers may like it",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            travelerBenefits.forEachIndexed { index, benefit ->
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (index != travelerBenefits.lastIndex) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.background
        ) {
            OutlinedButton(
                onClick = onSaveClick,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSaved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
                    .height(54.dp)
            ) {
                Text(
                    text = if (isSaved) {
                        "Saved"
                    } else {
                        "Save Gym"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    items: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            items.chunked(2).forEach { itemRow ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemRow.forEach { item ->
                        FeaturePill(
                            text = item,
                            isHighlighted = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
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
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TextButton(
                    onClick = onBack
                ) {
                    Text("Back")
                }
            }

            item {
                Column {
                    Text(
                        text = "Saved Gyms",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (gyms.isEmpty()) {
                            "Your saved places will appear here."
                        } else {
                            "${gyms.size} saved ${if (gyms.size == 1) "gym" else "gyms"}"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (gyms.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.45f
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Nothing saved yet",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Save a gym from your results to keep it easy to find for a future trip.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedButton(
                                onClick = onBack,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Explore Gyms",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                items(gyms) { gym ->
                    SavedGymCard(
                        gym = gym,
                        onClick = {
                            onGymClick(gym)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedGymCard(
    gym: Gym,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = gym.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = gym.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${gym.distanceMiles} miles away",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${gym.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (gym.isOpen) {
                            "Open now"
                        } else {
                            "Currently closed"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (gym.isOpen) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "$${gym.dayPassPrice} day pass",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "View details",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            gym.workoutTypes.chunked(3).forEach { workoutRow ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    workoutRow.forEach { workoutType ->
                        FeaturePill(
                            text = workoutType
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}