import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kizitonwose.calendar.core.CalendarMonth
import kotlinx.coroutines.delay
import kotlinx.datetime.YearMonth

@Composable
fun MonthHeader(
    month: CalendarMonth,
    isHighlighted: Boolean = false,
    onHighlightMonth: (YearMonth) -> Unit,
    onHighlightComplete: () -> Unit = {}
) {
    var showHighlight by remember { mutableStateOf(false) }
    var enableAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            enableAnimation = true
            showHighlight = true
            delay(10)
            showHighlight = false
            delay(1300) // match fade-out
            onHighlightComplete()
        }
    }

    val backgroundColor = if (enableAnimation) {
        val animatedColor by animateColorAsState(
            targetValue = if (showHighlight) Colors.divider else Colors.background,
            animationSpec = if (showHighlight) {
                tween(durationMillis = 1)
            } else {
                tween(durationMillis = 1300) // match fade-out
            },
            label = "monthHighlight"
        )
        animatedColor
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // .background(Colors.background)
            .aspectRatio(7f),
        contentAlignment = Alignment.Center

    ) {
        Text(
            text = "${month.yearMonth.month.name.lowercase()
                .replaceFirstChar { it.uppercase() }} ${month.yearMonth.year}",
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            // color = if (showHighlight) Color.White else Colors.textAccent,
            color = Colors.textAccent,

            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .padding(horizontal = 10.dp, vertical = 10.dp)
        )
    }
}
