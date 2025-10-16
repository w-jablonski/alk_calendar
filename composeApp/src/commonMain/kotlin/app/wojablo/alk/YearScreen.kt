import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalYearCalendar
import com.kizitonwose.calendar.compose.yearcalendar.YearContentHeightMode
import com.kizitonwose.calendar.compose.yearcalendar.rememberYearCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.ExperimentalCalendarApi
import com.kizitonwose.calendar.core.Year
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusYears
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCalendarApi::class)
@Composable
fun YearScreen(
    onMonth: (YearMonth) -> Unit = {},
    databaseHelper: DatabaseHelper,
) {
    val currentMonth = remember { YearMonth.now() }
    val currentYear = remember { Year(currentMonth.year) }
    val startYear = remember { currentYear.minusYears(200) }
    val endYear = remember { currentYear.plusYears(200) }
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val state = rememberYearCalendarState(
        startYear = startYear,
        endYear = endYear,
        firstVisibleYear = currentYear,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    // val selectedDatesMap by databaseHelper.selectedDates.collectAsState(initial = emptyMap())

    Scaffold(
        containerColor = Colors.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            VerticalYearCalendar(
                state = state,
                // modifier = Modifier.fillMaxSize(),
                // @IntRange = 3,
                calendarScrollPaged = true,
                // userScrollEnabled = false,
                // contentPadding = PaddingValues(bottom = 10.dp),
                contentPadding = PaddingValues(0.dp),
                yearBodyContentPadding = PaddingValues(0.dp),
                contentHeightMode = YearContentHeightMode.Wrap,
                monthVerticalSpacing = 5.dp,
                // monthHorizontalSpacing = if (isTablet) 52.dp else 10.dp,
                monthHorizontalSpacing = 10.dp,
                yearHeader = { year ->
                    YearHeader(year = year.year)
                },
                monthHeader = { month ->
                    MonthHeaderCompact(
                        month = month,
                        daysOfWeek = daysOfWeek,
                        onClick = { onMonth(month.yearMonth) }
                    )
                },
                monthBody = { month, content ->
                    Box(
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onMonth(month.yearMonth) } // clickable expects its lambda as the last positional argument
                            )
                    ) {
                        content()
                    }
                },
                dayContent = { day ->
                    // val isPrimarySelected = selectedDatesMap[day.date] == SelectionType.PRIMARY
                    // val isTemporarySelected = temporarySelectedDate == day.date
                    YearDay(
                        day = day,
                        today = today,
                    )
                }
            )
        }
    }
}

@Composable
private fun YearHeader(year: Year) {
    Text(
        text = year.value.toString(),
        fontSize = 19.sp,
        fontWeight = FontWeight.Medium,
        color = Colors.icon,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, bottom = 10.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun MonthHeaderCompact(
    month: CalendarMonth,
    daysOfWeek: List<DayOfWeek>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Text(
            text = month.yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Colors.textAccent,
            // textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth() // Give your Text a width to center within
                .padding(start = 5.dp, end = 5.dp, bottom = 4.dp)
        )
    }
}

@Composable
private fun YearDay(day: CalendarDay, today: LocalDate) {
    val isToday = day.date == today
    val isWeekend = day.date.dayOfWeek == DayOfWeek.SATURDAY ||
                    day.date.dayOfWeek == DayOfWeek.SUNDAY

    val backgroundColor = when {
        day.position != DayPosition.MonthDate -> Color.Transparent
        // isToday -> Colors.today
        else -> Color.Transparent
    }

    val textColor = when {
        day.position != DayPosition.MonthDate -> Color.Transparent
        isToday -> Colors.text
        // isWeekend -> Colors.textAccent // shade
        else -> Colors.text
    }

    val fontWeight = when {
        isToday -> FontWeight.Black
        // isWeekend -> FontWeight.Black // Medium
        else -> FontWeight.Medium
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .heightIn(min = 10.dp)
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp)
            // .offset(y = 1.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = day.date.day.toString(),
            fontSize = 8.sp,
            color = textColor,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                // .offset(y = (-5).dp)
                .padding(top = 0.dp, bottom = 0.dp)
        )
    }
}
