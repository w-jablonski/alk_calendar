import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
// import androidx.compose.runtime.remember
// import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.wojablo.alk.DatabaseDriverFactory

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    // val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    // val snackbarHostState = remember { SnackbarHostState() }

    // MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = primaryColor)) {
    MaterialTheme {
        NavHost(
            navController = navController,
            // navController: NavHostController = rememberNavController(),
            startDestination = "main"
        ) {
            composable("main") {
                MainScreen(
                    onNavigate = { navController.navigate("settings") }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}












