import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsHelper: SettingsHelper,
    onAbout: () -> Unit,
    onBack: () -> Unit
) {
    val currentTheme by settingsHelper.themeMode.collectAsState(initial = ThemeMode.FOLLOW_SYSTEM)

    Scaffold(
        containerColor = Colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Colors.icon
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAbout) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                             contentDescription = "About",
                             tint = Colors.icon
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colors.background,
                    titleContentColor = Colors.text
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Theme",
                fontSize = 20.sp,
                color = Colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(modifier = Modifier.selectableGroup()) {
                ThemeOption(
                    text = "Light",
                    selected = currentTheme == ThemeMode.BRIGHT,
                    onClick = { settingsHelper.setTheme(ThemeMode.BRIGHT) }
                )

                ThemeOption(
                    text = "Dark",
                    selected = currentTheme == ThemeMode.DARK,
                    onClick = { settingsHelper.setTheme(ThemeMode.DARK) }
                )

                ThemeOption(
                    text = "Follow System",
                    selected = currentTheme == ThemeMode.FOLLOW_SYSTEM,
                    onClick = { settingsHelper.setTheme(ThemeMode.FOLLOW_SYSTEM) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Colors.divider)
            Spacer(modifier = Modifier.height(24.dp))

            /*
            Text(
                text = "In progress...",
                fontSize = 14.sp,
                color = Colors.text,
                modifier = Modifier.padding(bottom = 16.dp)
            ) */
        }
    }
}

@Composable
private fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Colors.selected, // primaryColor // Colors.text
                unselectedColor = Colors.text // textSecondaryColor // Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = Colors.text
        )
    }
}
