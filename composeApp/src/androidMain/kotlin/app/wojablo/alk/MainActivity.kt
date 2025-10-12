package app.wojablo.alk

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()

            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            scrim = android.graphics.Color.TRANSPARENT,
                            darkScrim = android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            scrim = android.graphics.Color.TRANSPARENT,
                            darkScrim = android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }
            App(DatabaseDriverFactory(this))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(DatabaseDriverFactory(androidx.compose.ui.platform.LocalContext.current))
}
