
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
// import kotlinx.datetime.onDay
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.ui.tooling.preview.Preview

private val bgColor = Colors.myWhite
private val selectionColor = Colors.myAzure
private val todayColor = Color.LightGray
// private val defaultFontSize = 18.sp

@Composable
fun MainScreen(
    onNavigate: () -> Unit = {},
    // databaseHelper: DatabaseHelper,
    // settingsHelper: SettingsHelper,
    modifier: Modifier = Modifier
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val today = remember { LocalDate.now() }
    val firstDayOfWeek = remember { DayOfWeek.MONDAY }
    val daysOfWeek = remember { daysOfWeek() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.myWhite)
                .padding(1.dp)
        ) {
            Column {
                val state = rememberCalendarState(
                    startMonth = startMonth,
                    endMonth = endMonth,
                    firstVisibleMonth = currentMonth,
                    firstDayOfWeek = firstDayOfWeek
                )
                DaysOfWeekHeader(
                    daysOfWeek = daysOfWeek
                )
                VerticalCalendar(
                    state = state,
                    dayContent = { day ->
                        Day(
                            day = day,
                            today = today,
                            isSelected = selectedDate == day.date,
                            onClick = { clickedDay ->
                                if (clickedDay.position == DayPosition.MonthDate) {
                                    selectedDate = clickedDay.date
                                }
                            }
                        )
                    },
                    monthHeader = { month -> MonthHeader(month = month) }
                )
            }
        }
    }
}

@Composable
private fun DaysOfWeekHeader(
    daysOfWeek: List<DayOfWeek>
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    // .padding(1.dp)
                    .weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.name.take(3),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun MonthHeader(
    month: CalendarMonth,
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp),
            text = "${month.yearMonth.month.name
                .lowercase()
                .replaceFirstChar { it.uppercase() }} ${month.yearMonth.year}",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    isSelected: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .background(
                when {
                    day.position == DayPosition.MonthDate && isSelected -> selectionColor
                    day.position == DayPosition.MonthDate && day.date == today -> todayColor
                    else -> Color.Transparent
                }
            )
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
                // onClick(day.date)
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.day.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = when {
                day.position == DayPosition.MonthDate && isSelected -> Color.White
                day.position == DayPosition.MonthDate -> Color.Black
                // else -> Color.Gray
                else -> Color.Transparent
            }
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
























