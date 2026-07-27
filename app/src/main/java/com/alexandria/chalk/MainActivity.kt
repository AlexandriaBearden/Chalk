package com.alexandria.chalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexandria.chalk.ui.theme.ChalkTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip


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
    var animationStarted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        animationStarted = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 750,
            delayMillis = 100
        ),
        label = "landingContentAlpha"
    )

    val contentOffset by animateDpAsState(
        targetValue = if (animationStarted) 0.dp else 14.dp,
        animationSpec = tween(
            durationMillis = 750,
            delayMillis = 100
        ),
        label = "landingContentOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LandingAtmosphere()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 28.dp,
                    vertical = 24.dp
                )
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffset.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHALK",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 8.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Elevated workouts.\nEvery city.",
                    fontSize = 16.sp,
                    lineHeight = 23.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1.3f))

            Button(
                onClick = onFinished,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Find your fit.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LandingAtmosphere() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(
                    x = 145.dp,
                    y = (-85).dp
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.14f
                            ),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(390.dp)
                .offset(
                    x = (-185).dp,
                    y = 390.dp
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.24f
                            ),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.16f
                            )
                        )
                    )
                )
        )
    }
}

@Composable
fun ChalkHomeScreen(
    modifier: Modifier = Modifier,
    onFindGyms: (String, Set<String>) -> Unit,
    onSavedGymsClick: () -> Unit,
    onProfileClick: () -> Unit = {}
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

    val canSearch =
        destination.isNotBlank() && selectedOptions.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 20.dp
                )
        ) {
            HomeHeader(
                onProfileClick = onProfileClick
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Find the right gym,\nwherever you travel.",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                lineHeight = 39.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tell us where you're going and\nwhat matters for your workout.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            HomeSectionLabel(
                text = "DESTINATION"
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = { newDestination ->
                    destination = newDestination
                },
                placeholder = {
                    Text("Austin, TX")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (destination.isNotBlank()) {
                        IconButton(
                            onClick = {
                                destination = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear destination",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            HomeSectionLabel(
                text = "WORKOUT PREFERENCES"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Select everything you want included.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            workoutOptions.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    rowOptions.forEach { option ->
                        PreferenceTile(
                            text = option,
                            isSelected = option in selectedOptions,
                            onClick = {
                                selectedOptions =
                                    if (option in selectedOptions) {
                                        selectedOptions - option
                                    } else {
                                        selectedOptions + option
                                    }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(9.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onFindGyms(
                        destination.trim(),
                        selectedOptions
                    )
                },
                enabled = canSearch,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Find Gyms",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        ChalkBottomNavigation(
            selectedItem = ChalkBottomNavItem.SEARCH,
            onSearchClick = {},
            onSavedClick = onSavedGymsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PreferenceTile(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.38f
            )
        } else {
            MaterialTheme.colorScheme.background
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.65f
                )
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) {
                    Icons.Outlined.CheckCircle
                } else {
                    Icons.Outlined.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(17.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HomeHeader(
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CHALK",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.4.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onProfileClick,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun HomeSectionLabel(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

enum class ChalkBottomNavItem {
    SEARCH,
    SAVED,
    PROFILE
}

@Composable
fun ChalkBottomNavigation(
    selectedItem: ChalkBottomNavItem,
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedItem == ChalkBottomNavItem.SEARCH,
                onClick = onSearchClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                },
                label = {
                    Text("Search")
                },
                colors = chalkNavigationItemColors()
            )

            NavigationBarItem(
                selected = selectedItem == ChalkBottomNavItem.SAVED,
                onClick = onSavedClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Saved"
                    )
                },
                label = {
                    Text("Saved")
                },
                colors = chalkNavigationItemColors()
            )

            NavigationBarItem(
                selected = selectedItem == ChalkBottomNavItem.PROFILE,
                onClick = onProfileClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile"
                    )
                },
                label = {
                    Text("Profile")
                },
                colors = chalkNavigationItemColors()
            )
        }
    }
}

@Composable
private fun chalkNavigationItemColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.background,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

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

    val topRecommendation = rankedGyms.firstOrNull()
    val additionalGyms = rankedGyms.drop(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = "←",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Gyms in $destination",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = "Refine",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 12.dp)
            ) {
                items(selectedOptions.toList()) { option ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 8.dp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(
                    alpha = 0.65f
                )
            )
        }

        if (rankedGyms.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No close matches",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Try refining your preferences or broadening the destination.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Refine Search")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 22.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                topRecommendation?.let { (gym, matchCount) ->
                    item {
                        Text(
                            text = "TOP RECOMMENDATION",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GymResultCard(
                            gym = gym,
                            selectedOptions = selectedOptions,
                            matchCount = matchCount,
                            isFeatured = true,
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

                if (additionalGyms.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "MORE GREAT OPTIONS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(additionalGyms) { (gym, matchCount) ->
                        GymResultCard(
                            gym = gym,
                            selectedOptions = selectedOptions,
                            matchCount = matchCount,
                            isFeatured = false,
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
}

@Composable
fun GymResultCard(
    gym: Gym,
    selectedOptions: Set<String>,
    matchCount: Int,
    isFeatured: Boolean,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onGymClick: () -> Unit
) {
    val allFeatures = gym.workoutTypes + gym.amenities

    val matchedFeatures = selectedOptions.filter { option ->
        option in allFeatures
    }

    val matchPercentage = if (selectedOptions.isNotEmpty()) {
        (matchCount * 100) / selectedOptions.size
    } else {
        0
    }

    val recommendationReasons = buildRecommendationReasons(
        gym = gym,
        matchedFeatures = matchedFeatures
    )

    var reasonsExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        onClick = onGymClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(
            if (isFeatured) 22.dp else 18.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFeatured) 3.dp else 1.dp
        )
    ) {
        Column {
            if (isFeatured) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.42f
                            )
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 11.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECOMMENDED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "$matchPercentage% MATCH",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = if (isFeatured) 18.dp else 16.dp,
                    vertical = if (isFeatured) 18.dp else 15.dp
                )
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
                            style = if (isFeatured) {
                                MaterialTheme.typography.titleLarge
                            } else {
                                MaterialTheme.typography.titleMedium
                            },
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "${gym.location}  •  ${gym.distanceMiles} mi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "★ ${gym.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = if (gym.isOpen) {
                                "Open now"
                            } else {
                                "Currently closed"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (gym.isOpen) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(
                            onClick = onSaveClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text(
                                text = if (isSaved) "♥" else "♡",
                                fontSize = 23.sp,
                                color = if (isSaved) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "$${gym.dayPassPrice}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Day pass",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isFeatured) {
                    Spacer(modifier = Modifier.height(18.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(
                            alpha = 0.65f
                        )
                    )

                    TextButton(
                        onClick = {
                            reasonsExpanded = !reasonsExpanded
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 0.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Why we picked this",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = if (reasonsExpanded) "−" else "+",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = reasonsExpanded,
                        enter = fadeIn(
                            animationSpec = tween(220)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(160)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.24f
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(15.dp)
                        ) {
                            recommendationReasons.forEachIndexed { index, reason ->
                                Row(
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = reason,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (index != recommendationReasons.lastIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "$matchPercentage% match",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    gym.workoutTypes
                        .take(if (isFeatured) 3 else 2)
                        .forEach { workoutType ->
                            FeaturePill(
                                text = workoutType,
                                isHighlighted = workoutType in matchedFeatures
                            )
                        }
                }
            }
        }
    }
}

private fun buildRecommendationReasons(
    gym: Gym,
    matchedFeatures: List<String>
): List<String> {
    val reasons = mutableListOf<String>()

    if (matchedFeatures.isNotEmpty()) {
        val preferenceText = when (matchedFeatures.size) {
            1 -> "Matches your ${matchedFeatures.first()} preference"
            else -> "Matches ${matchedFeatures.size} of your selected preferences"
        }

        reasons.add(preferenceText)
    }

    if ("Day Pass" in gym.amenities) {
        reasons.add("Day passes are available without a membership")
    }

    if (gym.rating >= 4.8) {
        reasons.add("Highly rated by recent visitors")
    }

    if (gym.distanceMiles <= 1.5) {
        reasons.add("Conveniently located near your destination")
    }

    if ("Showers" in gym.amenities) {
        reasons.add("Showers make it easier to train during a travel day")
    }

    if ("Towels" in gym.amenities) {
        reasons.add("Towel service means one less thing to pack")
    }

    return reasons
        .distinct()
        .take(4)
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
    val recommendationReasons = buildGymDetailReasons(gym)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 22.dp,
                end = 22.dp,
                top = 10.dp,
                bottom = 30.dp
            )
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack,
                        contentPadding = PaddingValues(
                            horizontal = 2.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onSaveClick
                    ) {
                        Text(
                            text = if (isSaved) "♥" else "♡",
                            fontSize = 25.sp,
                            color = if (isSaved) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "CHALK RECOMMENDS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.3.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = gym.name,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${gym.location}  •  ${gym.distanceMiles} mi",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "★ ${gym.rating}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (gym.isOpen) {
                            "Open now"
                        } else {
                            "Currently closed"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (gym.isOpen) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.7f
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(26.dp))

                RecommendationDetailPanel(
                    reasons = recommendationReasons
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))

                DayPassDetailRow(
                    price = gym.dayPassPrice
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))

                QuietDetailSection(
                    title = "Workout options",
                    items = gym.workoutTypes
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))

                QuietDetailSection(
                    title = "Amenities",
                    items = gym.amenities
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.7f
                    )
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Before you go",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Day-pass availability and operating hours may change. Confirm directly with the gym before arriving.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 14.dp
                    )
                    .height(52.dp)
            ) {
                Text(
                    text = if (isSaved) {
                        "Saved to Collection"
                    } else {
                        "Save to Collection"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun RecommendationDetailPanel(
    reasons: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.32f
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.16f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "WHY WE PICKED THIS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            reasons.forEachIndexed { index, reason ->
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 21.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (index != reasons.lastIndex) {
                    Spacer(modifier = Modifier.height(11.dp))
                }
            }
        }
    }
}

@Composable
fun DayPassDetailRow(
    price: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Day pass",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "One-day access without a membership",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$$price",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.7f
            )
        )
    }
}

@Composable
fun QuietDetailSection(
    title: String,
    items: List<String>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    FeaturePill(
                        text = item
                    )
                }
            }

            Spacer(modifier = Modifier.height(9.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.7f
            )
        )
    }
}

private fun buildGymDetailReasons(
    gym: Gym
): List<String> {
    val reasons = mutableListOf<String>()

    when {
        gym.rating >= 4.8 -> {
            reasons.add("Exceptionally well rated by visitors")
        }

        gym.rating >= 4.5 -> {
            reasons.add("Consistently well rated by visitors")
        }
    }

    if ("Day Pass" in gym.amenities) {
        reasons.add("Offers flexible access without a long-term membership")
    }

    if (gym.distanceMiles <= 1.0) {
        reasons.add("Conveniently located less than one mile away")
    } else if (gym.distanceMiles <= 2.0) {
        reasons.add("Located within easy reach of your destination")
    }

    if (
        "Showers" in gym.amenities &&
        "Towels" in gym.amenities
    ) {
        reasons.add("Showers and towel service make it well suited to travel days")
    } else if ("Showers" in gym.amenities) {
        reasons.add("On-site showers make it easier to train between plans")
    }

    if ("Lockers" in gym.amenities) {
        reasons.add("Secure storage is available while you train")
    }

    if ("Parking" in gym.amenities) {
        reasons.add("Parking is available for travelers driving locally")
    }

    return reasons
        .distinct()
        .take(4)
}

@Composable
fun SavedGymsScreen(
    gyms: List<Gym>,
    onGymClick: (Gym) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHALK",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.4.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Saved Gyms",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = if (gyms.isEmpty()) {
                        "Your collection is ready when you are."
                    } else {
                        "${gyms.size} ${if (gyms.size == 1) "place" else "places"} saved for future trips."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (gyms.isEmpty()) {
                item {
                    EmptySavedGymsCard(
                        onExploreClick = onBack
                    )
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

        ChalkBottomNavigation(
            selectedItem = ChalkBottomNavItem.SAVED,
            onSearchClick = onBack,
            onSavedClick = {},
            onProfileClick = onProfileClick
        )
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.75f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${gym.location}  •  ${gym.distanceMiles} mi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.4f
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "★",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${gym.rating}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (gym.isOpen) {
                            "Open now"
                        } else {
                            "Currently closed"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (gym.isOpen) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$${gym.dayPassPrice} day pass",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "View details",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (gym.workoutTypes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.55f
                    )
                )

                Spacer(modifier = Modifier.height(13.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    gym.workoutTypes
                        .take(3)
                        .forEach { workoutType ->
                            CompactFeaturePill(
                                text = workoutType
                            )
                        }
                }
            }
        }
    }
}

@Composable
fun CompactFeaturePill(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}

@Composable
fun EmptySavedGymsCard(
    onExploreClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.25f
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.14f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "Your collection is empty",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "Save places that suit the way you train, then return to them whenever work brings you back.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onExploreClick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Explore Gyms",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}