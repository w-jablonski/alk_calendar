package app.wojablo.alk

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(DatabaseDriverFactory(this))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(DatabaseDriverFactory(androidx.compose.ui.platform.LocalContext.current))
}
