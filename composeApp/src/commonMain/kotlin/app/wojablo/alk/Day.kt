import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Day(
    day: CalendarDay,
    today: LocalDate,
    isPrimarySelected: Boolean,
    isTemporarySelected: Boolean,
    showRipple: Boolean = true,
    onClick: (CalendarDay) -> Unit,
    onLongClick: (CalendarDay) -> Unit,
) {
    val isWeekend = day.date.dayOfWeek == DayOfWeek.SATURDAY ||
                    day.date.dayOfWeek == DayOfWeek.SUNDAY
    val isToday = day.date == today

    val backgroundColor = when {
        day.position != DayPosition.MonthDate -> Color.Transparent
        isPrimarySelected -> Colors.selected
        isTemporarySelected -> Colors.clicked
        // isToday -> Colors.today
        // isWeekend -> Color(0xFFE0E0E0)
        else -> Color.Transparent
    }

    /*
    val backgroundAlphaColor = when {
        day.position != DayPosition.MonthDate -> Color.Transparent
        isToday && !isPrimarySelected && !isTemporarySelected -> Colors.today.copy(alpha = 0.05f)
        else -> Color.Transparent
    } */

    val continuousBackgroundColor = when {
        day.position != DayPosition.MonthDate -> Color.Transparent
        ////// isWeekend -> Colors.weekend
        else -> Color.Transparent
    }

    val textColor = when {
        day.position != DayPosition.MonthDate -> Color.Transparent
        isPrimarySelected || isTemporarySelected -> Colors.textOnSelected
        isToday -> Colors.today
        isWeekend -> Colors.textAccent //////
        else -> Colors.text
    }

    val fontWeight = when {
        isToday -> FontWeight.Black
        isPrimarySelected || isTemporarySelected -> FontWeight.Normal
        isWeekend -> FontWeight.Medium
        else -> FontWeight.Normal
    }
    // val fontWeight = if (isToday) FontWeight.Black else FontWeight.Normal
    // val fontWeight = FontWeight.Normal // Thin Light Normal Medium SemiBold Bold ExtraBold Black

    Box(
        modifier = Modifier
            // .weight(1f)
            .aspectRatio(1f)
            .background(continuousBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor)
                // .background(backgroundAlphaColor)
                .combinedClickable(
                    // enabled = day.position == DayPosition.MonthDate,
                    enabled = true,
                    onClick = { onClick(day) },
                    onLongClick = { onLongClick(day) },
                    /*
                    // This slows down single click badly
                    onDoubleClick = {
                        if (platform() == "desktop") onLongClick(day)
                    }, */
                    // indication = ripple(),
                    // indication = if (showRipple) ripple() else null,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.date.day.toString(),
                fontSize = Sizes.calendarFont,
                fontWeight = fontWeight,
                color = textColor
            )
        }
    }
}
