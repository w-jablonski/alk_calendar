import app.cash.sqldelight.db.SqlDriver
import app.wojablo.alk.CalendarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

enum class SelectionType(val value: String) {
    PRIMARY("PRIMARY"),
    TYPE_2("TYPE_2"),
    TYPE_3("TYPE_3"),
    TYPE_4("TYPE_4"),
    TYPE_5("TYPE_5"),
    TYPE_6("TYPE_6"),
    TYPE_7("TYPE_7"),
    TYPE_8("TYPE_8"),
    TYPE_9("TYPE_9"),
    TYPE_10("TYPE_10"),
    TYPE_11("TYPE_11"),
    TYPE_12("TYPE_12"),
    TYPE_13("TYPE_13"),
    TYPE_14("TYPE_14"),
    TYPE_15("TYPE_15");

    companion object {
        fun fromString(value: String): SelectionType {
            return entries.find { it.value == value } ?: PRIMARY
        }
    }
}

data class DateSelection(
    val date: LocalDate,
    val type: SelectionType
)

class DatabaseHelper(driver: SqlDriver) {
    private val database = CalendarDatabase(driver)
    private val queries = database.calendarDatabaseQueries
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _selectedDates = MutableStateFlow<Map<LocalDate, SelectionType>>(emptyMap())
    val selectedDates: Flow<Map<LocalDate, SelectionType>> = _selectedDates.asStateFlow()

    init {
        scope.launch {
            initializeDatabase()
            loadSelectedDates()
        }
    }

    private suspend fun initializeDatabase() {
        try {
            queries.createSelectedDatesTable()
            queries.createSettingsTable()
        } catch (e: Exception) {
            // Tables might already exist, ignore error
        }
    }

    private suspend fun loadSelectedDates() {
        val dates = queries.selectAllDates().executeAsList()
            .mapNotNull { (dateString, typeString) ->
                try {
                    val date = LocalDate.parse(dateString)
                    val type = SelectionType.fromString(typeString)
                    date to type
                } catch (e: Exception) {
                    null
                }
            }
            .toMap()
        _selectedDates.value = dates
    }

    fun toggleDateSelection(date: LocalDate, type: SelectionType = SelectionType.PRIMARY) {
        scope.launch {
            val dateString = date.toString()
            val currentDates = _selectedDates.value.toMutableMap()

            if (currentDates.containsKey(date)) {
                queries.deleteDate(dateString)
                currentDates.remove(date)
            } else {
                queries.insertDate(dateString, type.value)
                currentDates[date] = type
            }
            _selectedDates.value = currentDates
        }
    }

    fun setDateSelection(date: LocalDate, type: SelectionType) {
        scope.launch {
            val dateString = date.toString()
            val currentDates = _selectedDates.value.toMutableMap()
            queries.insertDate(dateString, type.value)
            currentDates[date] = type
            _selectedDates.value = currentDates
        }
    }

    fun removeDateSelection(date: LocalDate) {
        scope.launch {
            val dateString = date.toString()
            val currentDates = _selectedDates.value.toMutableMap()
            queries.deleteDate(dateString)
            currentDates.remove(date)
            _selectedDates.value = currentDates
        }
    }

    fun isDateSelected(date: LocalDate): Boolean {
        return _selectedDates.value.containsKey(date)
    }

    fun getDateSelectionType(date: LocalDate): SelectionType? {
        return _selectedDates.value[date]
    }

    fun getDatesByType(type: SelectionType): Set<LocalDate> {
        return _selectedDates.value.filter { it.value == type }.keys
    }
}

