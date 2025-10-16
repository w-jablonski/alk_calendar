import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

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
                    // contentAlignment = Alignment.Center
                ) {
                    // CircularProgressIndicator()
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
                            val currentMonth = remember { YearMonth.now() }
                            val startMonth = remember { currentMonth.minusMonths(10000) }
                            val endMonth = remember { currentMonth.plusMonths(10000) }

                            val calendarState = rememberCalendarState(
                                startMonth = startMonth,
                                endMonth = endMonth,
                                firstVisibleMonth = currentMonth,
                                firstDayOfWeek = DayOfWeek.MONDAY
                            )

                            var temporarySelectedDate by remember { mutableStateOf<LocalDate?>(null) }
                            var highlightedMonth by remember { mutableStateOf<YearMonth?>(null) }

                            MainScreenWithPager(
                                databaseHelper = dbHelper,
                                onSettings = { navController.navigate("settings") },
                                onAddEvent = { navController.navigate("addEvent") },
                                calendarState = calendarState,
                                temporarySelectedDate = temporarySelectedDate,
                                onTemporaryDateChange = { temporarySelectedDate = it },
                                highlightedMonth = highlightedMonth,
                                onHighlightMonth = { month -> highlightedMonth = month },
                                onHighlightComplete = { highlightedMonth = null }
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
                        // contentAlignment = Alignment.Center
                    ) {
                        // CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenWithPager(
    onSettings: () -> Unit = {},
    onAddEvent: () -> Unit = {},
    databaseHelper: DatabaseHelper,
    calendarState: CalendarState,
    temporarySelectedDate: LocalDate?,
    onTemporaryDateChange: (LocalDate?) -> Unit,
    highlightedMonth: YearMonth?,
    onHighlightMonth: (YearMonth) -> Unit,
    onHighlightComplete: () -> Unit

) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        // initialPageOffsetFraction = -1f,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()
    var showInfoPopup by remember { mutableStateOf(false) }
    var yearScreenReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100) // let MainScreen render first
        yearScreenReady = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MainScreen(
                    onSettings = onSettings,
                    onAddEvent = onAddEvent,
                    databaseHelper = databaseHelper,
                    calendarState = calendarState,
                    temporarySelectedDate = temporarySelectedDate,
                    onTemporaryDateChange = onTemporaryDateChange,
                    onShowInfo = { showInfoPopup = true },
                    highlightedMonth = highlightedMonth,
                    onHighlightComplete = onHighlightComplete,
                    onHighlightMonth = onHighlightMonth,
                )
                1 -> {
                    if (yearScreenReady) {
                        YearScreen(
                            databaseHelper = databaseHelper,
                            onMonth = { yearMonth ->
                                onHighlightMonth(yearMonth)
                                coroutineScope.launch {
                                    calendarState.scrollToMonth(yearMonth) // animateScrollToMonth
                                    pagerState.scrollToPage(0) // scrollToPage animateScrollToPage
                                }
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                            .fillMaxSize()
                            .background(Colors.background)
                        )
                    }
                }
            }
        }

        if (showInfoPopup) {
            InfoPopup(
                onDismiss = { showInfoPopup = false },
                // text = "Long-press a day to remember it"
            )
        }
    }
}
