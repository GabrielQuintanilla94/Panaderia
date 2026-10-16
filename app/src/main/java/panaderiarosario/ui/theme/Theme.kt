package panaderiarosario.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PanaderiaColors = lightColorScheme(
    primary = Color(0xFF6D4025),
    onPrimary = Color.White,
    secondary = Color(0xFF9B6A45),
    background = Color(0xFFFFFBF6),
    surface = Color(0xFFF8EFE5),
    onSurface = Color(0xFF2D211B)
)

@Composable
fun PanaderiaRosarioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PanaderiaColors,
        content = content
    )
}
