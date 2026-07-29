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
                    start = 22.dp,
                    end = 22.dp,
                    top = 18.dp,
                    bottom = 28.dp
                )
        ) {
            HomeHeader(
                onProfileClick = onProfileClick
            )

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "Find the right gym,\nwherever you travel.",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Search by destination, then tell Chalk what matters for this workout.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(0.92f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            HomeSectionLabel(
                text = "DESTINATION"
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = { newDestination ->
                    destination = newDestination
                },
                placeholder = {
                    Text(
                        text = "Austin, TX",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
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
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSectionLabel(
                text = "WORKOUT PREFERENCES"
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Choose everything you want your recommendation to include.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            workoutOptions.chunked(2).forEachIndexed { index, rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                if (index != workoutOptions.chunked(2).lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            AnimatedVisibility(
                visible = selectedOptions.isNotEmpty()
            ) {
                Text(
                    text = "${selectedOptions.size} selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {
                    onFindGyms(
                        destination.trim(),
                        selectedOptions
                    )
                },
                enabled = canSearch,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(
                    text = "Find My Gym",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (!canSearch) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        destination.isBlank() && selectedOptions.isEmpty() ->
                            "Add a destination and at least one preference."

                        destination.isBlank() ->
                            "Add your destination to continue."

                        else ->
                            "Choose at least one workout preference."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
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
        modifier = modifier
            .height(52.dp)
            .animateContentSize(),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 13.dp),
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
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(9.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
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
            letterSpacing = 3.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
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
        letterSpacing = 1.1.sp,
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

    if (isFeatured) {
        FeaturedGymResultCard(
            gym = gym,
            matchedFeatures = matchedFeatures,
            matchPercentage = matchPercentage,
            recommendationReasons = recommendationReasons,
            isSaved = isSaved,
            onSaveClick = onSaveClick,
            onGymClick = onGymClick
        )
    } else {
        CompactGymResultCard(
            gym = gym,
            matchedFeatures = matchedFeatures,
            matchPercentage = matchPercentage,
            isSaved = isSaved,
            onSaveClick = onSaveClick,
            onGymClick = onGymClick
        )
    }
}

@Composable
fun FeaturedGymResultCard(
    gym: Gym,
    matchedFeatures: List<String>,
    matchPercentage: Int,
    recommendationReasons: List<String>,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onGymClick: () -> Unit
) {
    var reasonsExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        onClick = onGymClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column {
            FeaturedGymVisual(
                gym = gym,
                matchPercentage = matchPercentage,
                isSaved = isSaved,
                onSaveClick = onSaveClick
            )

            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 18.dp
                )
            ) {
                Text(
                    text = gym.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = gym.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GymStatItem(
                        eyebrow = "RATING",
                        value = "★ ${gym.rating}",
                        modifier = Modifier.weight(1f)
                    )

                    GymStatItem(
                        eyebrow = "DISTANCE",
                        value = "${gym.distanceMiles} mi",
                        modifier = Modifier.weight(1f)
                    )

                    GymStatItem(
                        eyebrow = "DAY PASS",
                        value = "$${gym.dayPassPrice}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (gym.isOpen) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(2.dp)
                                .background(
                                    color = if (gym.isOpen) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    shape = CircleShape
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (gym.isOpen) {
                            "Open now"
                        } else {
                            "Currently closed"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (gym.isOpen) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                if (matchedFeatures.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(18.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(matchedFeatures.take(4)) { feature ->
                            FeaturePill(
                                text = feature,
                                isHighlighted = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.45f
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.16f
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = recommendationReasons
                                .firstOrNull()
                                ?: "A strong match for your selected preferences.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 21.sp
                        )

                        if (recommendationReasons.size > 1) {
                            Spacer(modifier = Modifier.height(6.dp))

                            TextButton(
                                onClick = {
                                    reasonsExpanded = !reasonsExpanded
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (reasonsExpanded) {
                                        "Show less"
                                    } else {
                                        "Why Chalk picked this"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = if (reasonsExpanded) "−" else "+",
                                    style = MaterialTheme.typography.titleSmall,
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
                            Column {
                                recommendationReasons
                                    .drop(1)
                                    .forEach { reason ->
                                        Spacer(modifier = Modifier.height(10.dp))

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
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 21.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "View gym details",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun FeaturedGymVisual(
    gym: Gym,
    matchPercentage: Int,
    isSaved: Boolean,
    onSaveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(174.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.32f
                        )
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .offset(
                    x = 210.dp,
                    y = (-45).dp
                )
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.24f
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(
                    x = (-35).dp,
                    y = 95.dp
                )
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.12f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = "CHALK RECOMMENDS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = gym.workoutTypes
                    .take(2)
                    .joinToString(" • "),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.92f
            )
        ) {
            Text(
                text = "$matchPercentage% match",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    horizontal = 13.dp,
                    vertical = 8.dp
                )
            )
        }

        Surface(
            onClick = onSaveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.92f
            )
        ) {
            Box(
                contentAlignment = Alignment.Center
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
        }
    }
}

@Composable
fun GymStatItem(
    eyebrow: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = eyebrow,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CompactGymResultCard(
    gym: Gym,
    matchedFeatures: List<String>,
    matchPercentage: Int,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onGymClick: () -> Unit
) {
    Card(
        onClick = onGymClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
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
                        text = "${gym.location} • ${gym.distanceMiles} mi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(
                        text = if (isSaved) "♥" else "♡",
                        fontSize = 22.sp,
                        color = if (isSaved) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "★ ${gym.rating}  •  $matchPercentage% match",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = if (gym.isOpen) {
                            "Open now"
                        } else {
                            "Currently closed"
                        },
                        style = MaterialTheme.typography.bodySmall,
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

                    Text(
                        text = "day pass",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (matchedFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    matchedFeatures
                        .take(2)
                        .forEach { feature ->
                            CompactFeaturePill(
                                text = feature
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