import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(
    onSettings: () -> Unit = {},
    onAddEvent: () -> Unit = {},
    databaseHelper: DatabaseHelper,
    calendarState: CalendarState, /////
    temporarySelectedDate: LocalDate?,
    onTemporaryDateChange: (LocalDate?) -> Unit,
    onShowInfo: () -> Unit = {},
    highlightedMonth: YearMonth?,
    onHighlightComplete: () -> Unit,
    onHighlightMonth: (YearMonth) -> Unit,
    // settingsHelper: SettingsHelper,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentMonth = remember { YearMonth.now() }
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val selectedDatesMap by databaseHelper.selectedDates.collectAsState(initial = emptyMap())

    Scaffold(
        containerColor = Colors.background,
        topBar = {
            MainTopBar(
                onSettings = onSettings,
                extraButton = ExtraButtonType.INFO,
                onInfo = { onShowInfo() },
                // onActions = { showActionsDialog = true },
                onToday = {
                    coroutineScope.launch {
                        // state.animateScrollToMonth(currentMonth)
                        calendarState.scrollToMonth(currentMonth)
                        onHighlightMonth(currentMonth)
                    }
                },
                onAddEvent = onAddEvent
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // .background(Colors.background)
                .padding(paddingValues)
                .padding(0.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onTemporaryDateChange(null)
                }
        ) {
            DaysOfWeekHeader(
                daysOfWeek = daysOfWeek
            )

            VerticalCalendar(
                // state = state, /////
                state = calendarState,
                // contentPadding = PaddingValues(bottom = 100.dp),
                dayContent = { day ->
                    val isPrimarySelected = selectedDatesMap[day.date] == SelectionType.PRIMARY
                    val isTemporarySelected = temporarySelectedDate == day.date

                    Day(
                        day = day,
                        today = today,
                        isPrimarySelected = isPrimarySelected,
                        isTemporarySelected = isTemporarySelected,
                        showRipple = day.position == DayPosition.MonthDate, // MonthDate, InDate, OutDate
                        onClick = { clickedDay ->
                            if (clickedDay.position == DayPosition.MonthDate) {
                                val hasPrimarySelection = selectedDatesMap[clickedDay.date] == SelectionType.PRIMARY
                                if (hasPrimarySelection) {
                                    return@Day
                                }
                                if (clickedDay.date != temporarySelectedDate) {
                                    // onTemporaryDateChange = clickedDay.date
                                    onTemporaryDateChange(clickedDay.date)
                                } else {
                                    // temporarySelectedDate = null
                                    onTemporaryDateChange(null)
                                }
                            } else {
                                // in-date or out-date
                                onTemporaryDateChange(null)
                            }
                        },
                        onLongClick = { longClickedDay ->
                            if (longClickedDay.position == DayPosition.MonthDate) {
                                // Toggle persistent selection
                                databaseHelper.toggleDateSelection(
                                    longClickedDay.date,
                                    SelectionType.PRIMARY
                                )
                                val hasPrimarySelection = selectedDatesMap[longClickedDay.date] == SelectionType.PRIMARY
                                if (hasPrimarySelection) {
                                    return@Day
                                }
                                // Clear temporary selection when making it permanent
                                /*
                                if (temporarySelectedDate == longClickedDay.date) {
                                    onTemporaryDateChange(null)
                                } */
                                onTemporaryDateChange(null)
                            }
                        }
                    )
                },
                // monthHeader = { month -> MonthHeader(month = month) }
                monthHeader = { month ->
                    MonthHeader(
                        month = month,
                        onHighlightMonth = onHighlightMonth,
                        isHighlighted = highlightedMonth == month.yearMonth,
                        onHighlightComplete = onHighlightComplete
                    )
                }
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader(
    daysOfWeek: List<DayOfWeek>
) {
    Row(
        modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null  // No ripple
        ) { /* consume the click */ }
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier
                .padding(top = 2.dp) // 12.dp
                .padding(bottom = 8.dp)
                .weight(1f),
                 textAlign = TextAlign.Center,
                 // text = dayOfWeek.name.take(3),
                 text = dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.titlecase() },
                 fontSize = 16.sp,
                 fontWeight = FontWeight.Normal,
                 color = Colors.text,
            )
        }
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = Colors.divider
    )
}

@Composable
fun InfoPopup(
    onDismiss: () -> Unit,
    // text: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.shade.copy(alpha = 0.3f))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            )
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(vertical = 170.dp, horizontal = 6.dp)
                .widthIn(max = 400.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Colors.surface
            ),
        ) {
            Text(
                // text = text,
                text = """Long-press a day to remember it

Swipe left to see the full year,
then tap a month to focus""",
                modifier = Modifier.padding(30.dp),
                // modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                // textAlign = TextAlign.Center,
                fontSize = Sizes.infoFont,
                color = Colors.text
            )
        }
    }
}

/*
@Composable
private fun ActionsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "About")
        },
        text = {
            Text(text = "Calendar App\nVersion 1.0\n\nA simple calendar application for managing your events.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
} */

@Preview
@Composable
private fun MainScreenPreview() {
    // MainScreen(databaseHelper = TODO())
}
