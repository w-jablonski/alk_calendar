import app.cash.sqldelight.db.SqlDriver
import app.wojablo.alk.CalendarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsHelper(driver: SqlDriver) {
    private val database = CalendarDatabase(driver)
    private val queries = database.calendarDatabaseQueries
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _themeMode = MutableStateFlow(ThemeMode.FOLLOW_SYSTEM)
    val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()

    init {
        scope.launch {
            initializeDatabase()
            loadTheme()
        }
    }

    private suspend fun initializeDatabase() {
        try {
            queries.createSettingsTable()
        } catch (e: Exception) {
            // Table might already exist
        }
    }

    private suspend fun loadTheme() {
        try {
            val theme = queries.selectSettingByKey("theme").executeAsOneOrNull()
            _themeMode.value = try {
                theme?.let { ThemeMode.valueOf(it) } ?: ThemeMode.FOLLOW_SYSTEM
            } catch (e: Exception) {
                ThemeMode.FOLLOW_SYSTEM
            }
        } catch (e: Exception) {
            _themeMode.value = ThemeMode.FOLLOW_SYSTEM
        }
    }

    fun setTheme(theme: ThemeMode) {
        scope.launch {
            queries.insertSetting("theme", theme.name)
            _themeMode.value = theme
        }
    }
}
