import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Grey1    = Color(0xFF111111)
val Grey2    = Color(0xFF222222)
val Grey3    = Color(0xFF333333)
val Grey4    = Color(0xFF444444)
val Grey5    = Color(0xFF555555)
val Grey6    = Color(0xFF666666)
val Grey7    = Color(0xFF777777)
val Grey8    = Color(0xFF888888)
val Grey9    = Color(0xFF999999)
val GreyA    = Color(0xFFAAAAAA)
val GreyB    = Color(0xFFBBBBBB)
val GreyC    = Color(0xFFCCCCCC)
val GreyD    = Color(0xFFDDDDDD)
val GreyE    = Color(0xFFEEEEEE)
val Whitish  = Color(0xFFF5F5F5)

val Azure          = Color(0xFF2196F3)
val Red            = Color(0xFFCF0000)
val Brown          = Color(0xFF644439)
val LightBrown     = Color(0xFF956251)


// #995533 #705f58 #886d63 #643e31 #956251
// #9F8000 #816317 #674a41 #998077 #644439

private val BrightColorScheme = AppColorScheme(
    background     = Whitish,
    surface        = Color.White,
    text           = Color.Black,
    icon           = Grey3,
    textOnSelected = Color.White,
    textAccent     = Color(0xFF886d63),
    weekend        = Color(0xFFE0E0E0),
    today          = Color.Black,
    clicked        = Grey9,
    selected       = LightBrown,
    shade          = Grey3,
    divider        = GreyC,
)

private val DarkColorScheme = AppColorScheme(
    background     = Grey2,
    surface        = Color.Black, // Color(0xFF444444), // Color(0xFF505050),
    text           = GreyE,
    icon           = GreyC, // Color(0xFFE0E0E0),
    textOnSelected = Color.White,
    textAccent     = Color(0xFF998077),
    weekend        = Grey3,
    today          = Color.White,
    clicked        = Grey4,
    selected       = Brown,
    shade          = Grey7,
    divider        = Grey3,
)

data class AppColorScheme(
    val background: Color,
    val surface: Color,
    val text: Color,
    val icon: Color,
    val textOnSelected: Color,
    val textAccent: Color,
    // val primary: Color,
    // val secondary: Color,
    // val accent: Color,
    val weekend: Color,
    val today: Color,
    val clicked: Color,
    val selected: Color,
    // val border: Color,
    // val error: Color,
    // val success: Color,
    // val warning: Color,
    // val disabled: Color,
    val shade: Color,
    val divider: Color,
)

val LocalAppColors = staticCompositionLocalOf { BrightColorScheme }

object Colors {
    val background: Color
        @Composable get() = LocalAppColors.current.background

    val surface: Color
        @Composable get() = LocalAppColors.current.surface

    val text: Color
        @Composable get() = LocalAppColors.current.text

    val icon: Color
        @Composable get() = LocalAppColors.current.icon

    val textOnSelected: Color
        @Composable get() = LocalAppColors.current.textOnSelected

    val textAccent: Color
        @Composable get() = LocalAppColors.current.textAccent

    val weekend: Color
        @Composable get() = LocalAppColors.current.weekend

    val today: Color
        @Composable get() = LocalAppColors.current.today

    val clicked: Color
        @Composable get() = LocalAppColors.current.clicked

    val selected: Color
        @Composable get() = LocalAppColors.current.selected

    val shade: Color
        @Composable get() = LocalAppColors.current.shade

    val divider: Color
        @Composable get() = LocalAppColors.current.divider
}

enum class ThemeMode {
    BRIGHT,
    DARK,
    FOLLOW_SYSTEM
}

@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.BRIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) DarkColorScheme else BrightColorScheme

    CompositionLocalProvider(
        LocalAppColors provides colorScheme,
    ) {
        content()
    }
}

object Sizes {
    // val topBarHeight = 56.dp
    val calendarFont = 18.sp
    val infoFont = 16.sp
}

/*
object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
} */

/*
fun Color.brighten(factor: Float = 0.5f): Color {
    return copy(
        red = red + (1f - red) * factor,
        green = green + (1f - green) * factor,
        blue = blue + (1f - blue) * factor,
        alpha = alpha
    )
} */

