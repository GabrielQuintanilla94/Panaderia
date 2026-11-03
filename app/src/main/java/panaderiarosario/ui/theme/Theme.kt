package panaderiarosario.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BakeryColors = lightColorScheme(
    primary = Color(0xFF6D4025), onPrimary = Color.White,
    secondary = Color(0xFF9B6A45), onSecondary = Color.White,
    background = Color(0xFFFFFBF6), onBackground = Color(0xFF2D211B),
    surface = Color(0xFFF8EFE5), onSurface = Color(0xFF2D211B),
    primaryContainer = Color(0xFFF5EBDD), onPrimaryContainer = Color(0xFF6D4025),
    secondaryContainer = Color(0xFFF5EBDD), onSecondaryContainer = Color(0xFF6D4025),
    tertiary = Color(0xFF9B6A45), tertiaryContainer = Color(0xFFF5EBDD),
    surfaceVariant = Color(0xFFF5EBDD), onSurfaceVariant = Color(0xFF6D4025),
    outline = Color(0xFF9B6A45), outlineVariant = Color(0xFFE5D6C7),
    error = Color(0xFFAB302C), errorContainer = Color(0xFFFFE9E5), onErrorContainer = Color(0xFF76201D)
)

private val BakeryTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp)
)

@Composable
fun PanaderiaRosarioTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = BakeryColors, typography = BakeryTypography,
        shapes = Shapes(extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp), large = RoundedCornerShape(24.dp), extraLarge = RoundedCornerShape(28.dp)),
        content = content)
}
