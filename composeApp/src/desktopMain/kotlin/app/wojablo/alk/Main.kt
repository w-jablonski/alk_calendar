
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Alk Calendar",
        state = rememberWindowState(
            width = 380.dp,
            height = 600.dp
        )
    ) {
        App()
    }
}
