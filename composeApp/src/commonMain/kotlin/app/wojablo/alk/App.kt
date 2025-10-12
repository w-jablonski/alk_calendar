import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.wojablo.alk.CalendarDatabase
import app.wojablo.alk.DatabaseDriverFactory
import kotlinx.coroutines.launch

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    var databaseHelper by remember { mutableStateOf<DatabaseHelper?>(null) }
    var settingsHelper by remember { mutableStateOf<SettingsHelper?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    LaunchedEffect(driverFactory) {
        coroutineScope.launch {
            try {
                val driver = driverFactory.createDriver(
                    schema = CalendarDatabase.Schema,
                    name = "calendar_database"
                )
                if (driver != null) {
                    databaseHelper = DatabaseHelper(driver)
                    settingsHelper = SettingsHelper(driver)
                }
                isLoading = false
            } catch (e: Exception) {
                println("Database initialization failed: ${e.message}")
                isLoading = false
            }
        }
    }

    // val settings = settingsHelper?.settings?.collectAsState(initial = AppSettings())
    // val userThemeMode = settings?.value?.theme ?: ThemeMode.FOLLOW_SYSTEM
    val themeMode = settingsHelper?.themeMode?.collectAsState(initial = ThemeMode.FOLLOW_SYSTEM)
    val userThemeMode = themeMode?.value ?: ThemeMode.FOLLOW_SYSTEM

    val isDarkTheme = when (userThemeMode) {
        ThemeMode.BRIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }

    LaunchedEffect(isDarkTheme) {
        ThemeManager.updateTheme(isDarkTheme)
    }

    AppTheme(themeMode = userThemeMode) {
        MaterialTheme {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val dbHelper = databaseHelper
                val sHelper = settingsHelper
                if (dbHelper != null && sHelper != null) {
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            MainScreen(
                                databaseHelper = dbHelper,
                                onSettings = { navController.navigate("settings") },
                                onAddEvent = { navController.navigate("addEvent") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                settingsHelper = sHelper,
                                onAbout = { navController.navigate("about") },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("addEvent") {
                            AddEventScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

