import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import alk.composeapp.generated.resources.Res
import alk.composeapp.generated.resources.info_svgrepo_com4
import alk.composeapp.generated.resources.plus_svgrepo_com
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onSettings: () -> Unit,
    extraButton: ExtraButtonType = ExtraButtonType.NONE,
    onInfo: () -> Unit = {},
    onActions: () -> Unit = {},
    onToday: () -> Unit,
    onAddEvent: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            TextButton(onClick = onToday) {
                Text(
                    text = "Show Today",
                    color = Colors.textAccent,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(y = 2.dp),
                    fontSize = 17.sp
                )
            }
        },
        navigationIcon = {
            Row {
                IconButton(onClick = onSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Colors.icon
                    )
                }
                when (extraButton) {
                    ExtraButtonType.INFO -> {
                        IconButton(
                            modifier = Modifier.width(30.dp),
                            onClick = onInfo
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.info_svgrepo_com4),
                                modifier = Modifier.size(18.dp),
                                contentDescription = "Info",
                                tint = Colors.icon
                            )
                        }
                    }
                    ExtraButtonType.ACTIONS -> {
                        IconButton(
                            modifier = Modifier.width(30.dp),
                            onClick = onActions
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Actions",
                                tint = Colors.icon
                            )
                        }
                    }
                    ExtraButtonType.NONE -> { }
                }
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .width(78.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = onAddEvent,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.plus_svgrepo_com),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Add Event",
                    tint = Colors.icon
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Colors.background,
            //navigationIconContentColor = Colors.icon,
            //titleContentColor = Colors.textAccent,
            //actionIconContentColor = Colors.icon
        )
    )
}

enum class ExtraButtonType {
    NONE,
    INFO,
    ACTIONS
}
